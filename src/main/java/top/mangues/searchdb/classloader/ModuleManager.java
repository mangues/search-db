package top.mangues.searchdb.classloader;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ModuleManager {
    /**
     * 用来并发加载多个模块jar
     */
    private ExecutorService executorService = Executors.newFixedThreadPool(8);

    /**
     * cache用来存放所有Jar里面的Class对象
     */
    @SuppressWarnings("rawtypes")
    private Map<String, Class> cache = new ConcurrentHashMap<String, Class>();

    /**
     * moduleList是需要加载的模块的Jar的路径
     */
    private List<String> moduleList = new ArrayList<String>();

    /**
     * 核心方法:其中future的call方法针对每个模块新建了一个URLClassLoader加载器用来加载该模块的类,并把加载的类放入内存map.
     */
    public void init() {
        System.out.println("----begin load all module----");
        List<Future<String>> futureList = new ArrayList<Future<String>>();
        for (String module : moduleList) {
            Future<String> future = executorService.submit(new Callable<String>() {

                @Override
                public String call() throws Exception {
                    try {
                        // URL[] moduleURL = new URL[] { new URL("file://"
                        // +module) };
                        File moduleFile = new File(module);
                        URL moduleURL = moduleFile.toURI().toURL();
                        URL[] moduleUrl = new URL[] { moduleURL };

                        @SuppressWarnings("resource")
                        URLClassLoader classLoader = new URLClassLoader(moduleUrl);
                        @SuppressWarnings("resource")
                        JarFile jar = new JarFile(new File(module));
                        Enumeration<JarEntry> entries = jar.entries();
                        while (entries.hasMoreElements()) {
                            JarEntry entry = entries.nextElement();
                            String className = getClassName(entry);
                            if (className == null || className.length() == 0) {
                                continue;
                            }

                            try {
                                Class<?> clazz = classLoader.loadClass(className);
                                cache.put(className, clazz);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return module;
                }
            });
            futureList.add(future);
        }

        for (Future<String> future : futureList) {
            try {
                String moduleName = future.get();
                System.out.println("----load module " + moduleName + "ok");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        executorService.shutdown();
        System.out.println("----end load all module----");
    }

    private String getClassName(JarEntry jarEntry) {
        String jarName = jarEntry.getName();

        if (!jarName.endsWith(".class")) {
            return null;
        }
        if (jarName.charAt(0) == '/') {
            jarName = jarName.substring(1);
        }
        jarName = jarName.replace("/", ".");
        return jarName.substring(0, jarName.length() - 6);
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public Map<String, Class> getCache() {
        return cache;
    }

    public void setCache(Map<String, Class> cache) {
        this.cache = cache;
    }

    public List<String> getModuleList() {
        return moduleList;
    }

    public void setModuleList(List<String> moduleList) {
        this.moduleList = moduleList;
    }

}
