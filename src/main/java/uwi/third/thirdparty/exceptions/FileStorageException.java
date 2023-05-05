package uwi.third.thirdparty.exceptions;

import org.springframework.stereotype.Service;

@Service
public class FileStorageException extends RuntimeException {
    
	private static final long serialVersionUID = 8784591853580099944L;

	public FileStorageException() {
		
	}
	public FileStorageException(String message) {
        super(message);
    }

    public FileStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
