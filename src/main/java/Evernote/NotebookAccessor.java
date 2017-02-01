package Evernote;

import com.evernote.clients.NoteStoreClient;
import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.notestore.NoteCollectionCounts;
import com.evernote.edam.notestore.NoteFilter;
import com.evernote.edam.notestore.NoteList;
import com.evernote.edam.type.Note;
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

    public int countNotesWithTags(String searchTags) throws EDAMUserException, EDAMSystemException, TException, EDAMNotFoundException {

        StringTokenizer tagsFromSearchTerm = new StringTokenizer(searchTags, ",");

        if (tagsFromSearchTerm.countTokens() == 0) {
            return getNoteCountForFilter(emptyFilterForNotbook()) ;
        }

        return getNoteCountForFilter(filterForSearchTags(searchTags)) ;
    }

    private NoteFilter emptyFilterForNotbook() {

        NoteFilter filter = new NoteFilter();
        filter.setNotebookGuid(notebook.getGuid());
        return filter ;
    }

    private int getNoteCountForFilter(NoteFilter filter) throws EDAMUserException, EDAMSystemException, TException, EDAMNotFoundException {

        NoteCollectionCounts noteCollectionCounts = noteStore.findNoteCounts(filter, false) ;
        return noteCollectionCounts.getNotebookCounts().get(notebook.getGuid());
    }

    private List<String> getNotesForFilter(NoteFilter filter) throws EDAMUserException, EDAMSystemException, TException, EDAMNotFoundException {

        List<String> notes = new ArrayList<>() ;

        NoteList noteList = noteStore.findNotes(filter, 0, 100) ;
        List<Note> notesFromStore = noteList.getNotes() ;

        for (Note note : notesFromStore) {
            notes.add(note.getContent()) ;
        }

        return notes ;

    }

    private NoteFilter filterForSearchTags(String searchTags) throws TException, EDAMUserException, EDAMSystemException {

        List<String> tagGuids = new ArrayList<>() ;
        List<Tag> storeTagList = noteStore.listTags() ;

        for (Tag tagFromStore: storeTagList) {
            StringTokenizer tagsFromSearchTerm = new StringTokenizer(searchTags, ",") ;
            while (tagsFromSearchTerm.hasMoreTokens()) {

                if(tagFromStore.getName().equalsIgnoreCase(tagsFromSearchTerm.nextToken())) {
                    tagGuids.add(tagFromStore.getGuid()) ;
                }
            }
        }

        NoteFilter filter = emptyFilterForNotbook();
        filter.setTagGuids(tagGuids);

        return filter;
    }

    public List<String> getNotesWithTags(String searchTags) throws EDAMUserException, EDAMSystemException, EDAMNotFoundException, TException {

        StringTokenizer tagsFromSearchTerm = new StringTokenizer(searchTags, ",");

        if (tagsFromSearchTerm.countTokens() == 0) {
            return getNotesForFilter(emptyFilterForNotbook()) ;
        }

        return getNotesForFilter(filterForSearchTags(searchTags)) ;
    }
}
