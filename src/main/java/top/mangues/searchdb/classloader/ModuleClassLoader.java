package top.mangues.searchdb.classloader;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author mangues【mangues@yeah.net】
 * @Date  2018/12/12 2:11 PM
 * @Description 自定义类加载器
 */
public class ModuleClassLoader extends URLClassLoader {
    private ModuleManager manager = new ModuleManager();

    public ModuleClassLoader(URL[] urls, ModuleManager manager) {
        super(urls);
        this.manager = manager;
    }

    /**
     * 重写了loadClass,优先从内存map查找要加载的类
     */
    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        if (manager.getCache().containsKey(name)) {
            return manager.getCache().get(name);
        } else {
            return super.loadClass(name, resolve);
        }
    }
}
