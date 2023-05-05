package uwi.third.thirdparty.service;

import org.springframework.stereotype.Service;

import lombok.Getter;
import lombok.Setter;

@Service
@Getter @Setter
public class UploadFileResponse {
    private String fileName;
    private String fileDownloadUri;
    private String fileType;
    private long size;

    public UploadFileResponse() {
    	
    }
    public UploadFileResponse(String fileName, String fileDownloadUri, String fileType, long size) {
        this.fileName = fileName;
        this.fileDownloadUri = fileDownloadUri;
        this.fileType = fileType;
        this.size = size;
    }

}
