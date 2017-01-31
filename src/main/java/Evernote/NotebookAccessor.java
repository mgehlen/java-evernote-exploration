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
 * Notebookaccessor is used to private an interface to Evernote, which does not lead the business code to deal with all of the Evernote Data Structure.
 * So you can Evernote just ask for certain numbers and interpret them, or ask for e set of notes with tags to work with them. You don't need to know how Evernote works to create code for exploratory testing visualisation.
 */
@SuppressWarnings("WeakerAccess")
public class NotebookAccessor {

    private Notebook notebook ;
    private final NoteStoreClient noteStore ;

    public NotebookAccessor(NoteStoreClient noteStore, String notebookName) throws EDAMUserException, TException, EDAMSystemException, EDAMNotFoundException {
        this.noteStore = noteStore ;
        setNotebookByName(notebookName);
    }

    @SuppressWarnings("WeakerAccess")
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


    public int countNotesWithTags(String tagsString) throws EDAMUserException, EDAMSystemException, TException, EDAMNotFoundException {

        StringTokenizer tags = new StringTokenizer(tagsString, ",");

        List<String> tagGuids = new ArrayList<String>() ;

        NoteFilter filter = new NoteFilter();
        filter.setNotebookGuid(notebook.getGuid());

        if (tags.countTokens() == 0) {
            NoteCollectionCounts noteCollectionCounts = noteStore.findNoteCounts(filter, false) ;
            return noteCollectionCounts.getNotebookCounts().get(notebook.getGuid());
        }

        List<Tag> tagList = noteStore.listTags() ;

        for (Tag tag: tagList) {
            tags = new StringTokenizer(tagsString, ",") ;
            while (tags.hasMoreTokens()) {

                if(tag.getName().equalsIgnoreCase(tags.nextToken())) {
                    tagGuids.add(tag.getGuid()) ;
                }
            }
        }

        filter.setTagGuids(tagGuids);
        NoteCollectionCounts noteCollectionCounts = noteStore.findNoteCounts(filter, false) ;
        return noteCollectionCounts.getNotebookCounts().get(notebook.getGuid());

    }
}
