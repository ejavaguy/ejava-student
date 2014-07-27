package ejava.examples.orm.rel.annotated;

import javax.persistence.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ejava.examples.orm.rel.MediaCopyPK;

/**
 * This class provides an example of a ManyToOne mapping. The media copy owns
 * the relationship to the media. Therefore the foreign key to media is within
 * the media copy database table.<p/>
 * 
 * This class has an unrelated technical issue worth mentioning. 
 * Everything would have worked out fine if we created a simple primary key
 * field for the media copy and a separate foreign key to the media. Instead, 
 * we pretended to encounter a composite primary key where one of the values
 * doubled as the foreign key to the media.<p/>
 * 
 * <pre>
 * MediaCopy (pk=MEDIACOPY_MID + COPYNO)
 *     MEDIACOPY_MID (fk to Media.MEDIA_ID) 
 *     COPYNO
 *     ...
 * Media (pk=MEDIA_ID)
 *     MEDIA_ID
 *     ...
 * </pre>
 * 
 * The MediaCopy now has two uses for the MEDIACOPY_MID column. One as a primary
 * key (part of a compound primary key) and the other as the foreign key to the Media.
 * We need to make sure the provider preserves the semantics of the primary key while
 * reusing it from the foreign key.
 * 
 * This implementation will use the JPA 1.0 technique of mapping both the 
 * primary and foreign key properties to the same column and then mark the 
 * foreign key as READ-ONLY using the insertable and updatable properties
 * of the @Column.
 */

@Entity @Table(name="ORMREL_MEDIACOPY")
@IdClass(MediaCopyPK.class)
public class MediaCopy {
    private static final Log log = LogFactory.getLog(MediaCopy.class);
    @Id //mapped to COPY_NO by IdClass
    private int copyNo;    
    @Id //mapped to MEDIACOPY_MID by IdClass
    private long mediaId;    
    @ManyToOne
    @JoinColumn(name="MEDIACOPY_MID", //mapped same as mediaId property
            insertable=false, updatable=false) //makes column read-only
    private Media media;
    
    @SuppressWarnings("unused")
    private MediaCopy() { log.debug(super.toString() + ": ctor()"); }
    public MediaCopy(Media media, int copyNo) {
        log.debug(super.toString() + ": ctor() mediaId="
                + media.getId() + ", copyNo=" + copyNo);
        setMedia(media);
        setMediaId(media.getId());
        setCopyNo(copyNo);
    }    

    public int getCopyNo()                { return copyNo; }    
    private void setCopyNo(int copyNo)    { this.copyNo = copyNo; }
    
    public long getMediaId()              { return mediaId; }
    private void setMediaId(long mediaId) { this.mediaId = mediaId; }
    
    public Media getMedia()               { return media; }    
    private void setMedia(Media media)    { this.media = media; }

    public String toString() {
        return super.toString() +
            ", mediaId=" + mediaId +
            ", copyNo=" + copyNo +
            ", media=" + media;
    }
}
