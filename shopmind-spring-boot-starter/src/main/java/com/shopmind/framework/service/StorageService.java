package com.shopmind.framework.service;

import com.shopmind.framework.model.FileObject;
import com.shopmind.framework.model.FilePart;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.CompletedPart;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface StorageService {
    /**
     * 上传文件
     * @param file 文件
     * @return 上传后，文件的 url
     */
    String uploadFile(MultipartFile file);

    /**
     * 下载文件
     * @param fileName 文件名
     * @return 文件字节数组
     */
    byte[] downloadFile(String fileName);

    /**
     * 删除文件
     * @param fileName 文件名
     */
    void deleteFile(String fileName);

    /**
     * 列出此服务存储的所有文件
     * @return FileObject
     */
    List<FileObject> listFiles();

    /**
     * 按前缀过滤列出文件
     * @return FileObject
     */
    List<FileObject> listFiles(String prefix);

    /**
     * 后端处理分片，不需要前端配合
     * @param file 文件
     * @return 文件 url
     */
    String uploadLargeFile(MultipartFile file);

    // ============= 前端分片，后端配合，分片上传大型文件（现在应用的标准方式） =============

    /**
     * 初始化分片上传的请求，返回 uploadId
     * @param fileName 文件名
     * @return uploadId
     */
    String initiateMultipartUpload(String fileName);

    /**
     * 上传分片
     * @param fileName 文件名
     * @param uploadId 初始化分片上传请求时，返回的 uploadId
     * @param partNumber  分片的序号
     * @param inputStream  分片字节流
     * @param size  分片大小
     * @return 分片完成情况（重要参数：completedPart.partNumber() 和 completedPart.eTag()）
     */
    CompletedPart uploadPart(String fileName, String uploadId, int partNumber, InputStream inputStream, long size);

    /**
     * 合并文件分片
     * @param fileName  文件名
     * @param uploadId 初始化分片上传请求时，返回的 uploadId
     * @param parts 每一个分片对象
     */
    String completeMultipartUpload(String fileName, String uploadId, List<FilePart> parts);

    /**
     * 取消分片上传
     * @param fileName 文件名
     * @param uploadId  分片初始化时返回的 uploadId
     */
    void cancelMultipartUpload(String fileName, String uploadId);
}
