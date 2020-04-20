package com.wyf.vfs;

/**
 * @author wangyaofeng
 *
 */
public class VfsException extends RuntimeException {
	
	public VfsException() {
	    super();
	  }

	  public VfsException(String message, Throwable cause) {
	    super(message, cause);
	  }

	  public VfsException(Throwable cause) {
	    super(cause);
	  }

	  public VfsException(String message) {
	    super(message);
	  }
	
}
