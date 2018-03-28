package noteData;

import java.util.UUID;

/**
 * Created by PErry on 2018/2/15.
 */

public class NoteBean {

    public String id;
    public String content;

    public NoteBean(String content){
        this.id = UUID.randomUUID().toString();     /*保持id唯一性*/
        this.content = content;
    }

    public NoteBean(String id, String content){
        this.id = id;
        this.content = content;
    }
}
