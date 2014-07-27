package ejava.examples.orm.rel;

import java.io.Serializable;

import javax.persistence.Column;

public class MediaCopyPK implements Serializable {
    private static final long serialVersionUID = 1L;
    @Column(name="MEDIACOPY_MID")
    private long mediaId;
    @Column(name="COPY_NO")    
    private int copyNo;
    
    public MediaCopyPK() {}
    public MediaCopyPK(long mediaId, int copyNo) {
        this.mediaId = mediaId;
        this.copyNo = copyNo;
    }
    public int getCopyNo() { return copyNo; }
    public void setCopyNo(int copyNo) {
        this.copyNo = copyNo;
    }
    
    public long getMediaId() { return mediaId; }
    public void setMediaId(long mediaId) {
        this.mediaId = mediaId;
    }
    
    public int hashCode() {
        return (int)mediaId + copyNo;
    }    
    public boolean equals(Object obj) {
        try {
            if (obj == this) return true;
            return ((MediaCopyPK)obj).mediaId == mediaId &&
                   ((MediaCopyPK)obj).copyNo == copyNo;
        }
        catch (Throwable ex) {
            return false;
        }
    }
}
