package com.atguigu.gmall.product.util;

import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件管理的工具包
 */
public class FastDfsUtil {

    /**
     * 在这个类初始化的时候就加载这个配置
     */
    static {
        try {
            //加载配置文件
            ClassPathResource resource = new ClassPathResource("abc.conf");
            //fastdfs初始化
            ClientGlobal.init(resource.getPath());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 文件上传
     * @param file
     * @return
     * @throws Exception
     */
    public static String fileUpload(MultipartFile file) throws Exception{
        //获取可用的storage的信息
        StorageClient storageClient = FastDfsUtil.getStorageClient();
        //获取文件的名字: 123.jpg
        String originalFilename = file.getOriginalFilename();
        //通过storage进行文件的上传
        /**
         * 1.文件的字节码
         * 2.文件的拓展名
         * 3.附加参数: 时间 地点...水印
         */
        String[] strings = storageClient.upload_file(
                file.getBytes(),
                StringUtils.getFilenameExtension(originalFilename),
                null);
        //返回
        return strings[0] + "/" + strings[1];
    }

    /**
     * 下载: 作业-----将下载好的文件保存到电脑的d:/ 文件的名字叫a.png/jpg
     * @return
     */
    public static byte[] downLoad(String group, String path) throws Exception{
        //获取可用的storage的信息
        StorageClient storageClient = FastDfsUtil.getStorageClient();
        //文件下载
        return storageClient.download_file(group, path);
    }

    /**
     * 删除
     * @param group
     * @param path
     * @return
     * @throws Exception
     */
    public static Boolean delete(String group, String path) throws Exception{
        //获取可用的storage的信息
        StorageClient storageClient = FastDfsUtil.getStorageClient();
        //删除
        int i = storageClient.delete_file(group, path);
        //返回
        return i==0?true:false;
    }

    /**
     * 获取可用的StorageClient
     * @return
     */
    private static StorageClient getStorageClient() throws Exception{
        //tracker初始化
        TrackerClient trackerClient = new TrackerClient();
        //通过tracker创建连接
        TrackerServer connection = trackerClient.getConnection();
        //获取可用的storage的信息
        return new StorageClient(connection, null);
    }
}
