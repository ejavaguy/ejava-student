package ejava.examples.orm.rel;

import java.io.Serializable;

public class MediaCopyPK2 implements Serializable {
    private static final long serialVersionUID = 1L;
    private long media;
    private int copyNo;
    
    public MediaCopyPK2() {}
    public MediaCopyPK2(long mediaId, int copyNo) {
        this.media = mediaId;
        this.copyNo = copyNo;
    }
    public int getCopyNo() { return copyNo; }
    public void setCopyNo(int copyNo) {
        this.copyNo = copyNo;
    }
    
    public long getMediaId() { return media; }
    public void setMediaId(long mediaId) {
        this.media = mediaId;
    }
    
    public int hashCode() {
        return (int)media + copyNo;
    }    
    public boolean equals(Object obj) {
        try {
            if (obj == this) return true;
            return ((MediaCopyPK2)obj).media == media &&
                   ((MediaCopyPK2)obj).copyNo == copyNo;
        }
        catch (Throwable ex) {
            return false;
        }
    }
}
