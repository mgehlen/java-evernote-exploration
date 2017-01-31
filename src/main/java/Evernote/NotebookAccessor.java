package Evernote;

import com.evernote.clients.NoteStoreClient;
import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.notestore.NoteCollectionCounts;
import com.evernote.edam.notestore.NoteFilter;
import com.evernote.edam.type.Notebook;
import com.evernote.edam.type.Tag;
import com.evernote.thrift.TException;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by marcelg on 31/12/16 as Appium PoC.
 */
public class NotebookAccessor {

    private Notebook notebook ;
    private NoteStoreClient noteStore ;

    public NotebookAccessor(NoteStoreClient noteStore, String notebookName) throws EDAMUserException, TException, EDAMSystemException, EDAMNotFoundException {
        this.noteStore = noteStore ;
        setNotebookByName(notebookName);
    }

    public void setNotebookByName(String notebookName) throws TException, EDAMUserException, EDAMSystemException, EDAMNotFoundException {
        List<Notebook> notebooks = noteStore.listNotebooks() ;

        for (Notebook notebook : notebooks) {
            if (notebook.getName().equalsIgnoreCase(notebookName)) {
                this.notebook = noteStore.getNotebook(notebook.getGuid()) ;
                return ;
            }
        }

        throw new  EDAMNotFoundException() ;
    }


    public int countNotesWithTags(String tagsString, String delimiter, boolean withTrash) throws EDAMUserException, EDAMSystemException, TException, EDAMNotFoundException {

        StringTokenizer tags = new StringTokenizer(tagsString, delimiter);

        List<String> tagGuids = new ArrayList<String>() ;

        NoteFilter filter = new NoteFilter();
        filter.setNotebookGuid(notebook.getGuid());

        if (tags.countTokens() == 0) {
            NoteCollectionCounts noteCollectionCounts = noteStore.findNoteCounts(filter, withTrash) ;
            return noteCollectionCounts.getNotebookCounts().get(notebook.getGuid()).intValue() ;
        }

        List<Tag> tagList = noteStore.listTags() ;

        for (Tag tag: tagList) {
            tags = new StringTokenizer(tagsString, delimiter) ;
            while (tags.hasMoreTokens()) {

                if(tag.getName().equalsIgnoreCase(tags.nextToken())) {
                    tagGuids.add(tag.getGuid()) ;
                }
            }
        }

        filter.setTagGuids(tagGuids);
        NoteCollectionCounts noteCollectionCounts = noteStore.findNoteCounts(filter, withTrash) ;
        return noteCollectionCounts.getNotebookCounts().get(notebook.getGuid()).intValue() ;

    }
}
