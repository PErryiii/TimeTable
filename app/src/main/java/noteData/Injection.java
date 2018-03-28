package noteData;

import android.content.Context;

import noteData.local.NoteLocalDataSource;

/**
 * Created by PErry on 2018/2/23.
 */

public class Injection {
    public static NoteRepository provideRepository(Context context){
        return NoteRepository.getINSTANCE(NoteLocalDataSource.getInstance(context));
    }
}
