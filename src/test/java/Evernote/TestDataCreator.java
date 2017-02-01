package Evernote;

import com.evernote.edam.notestore.NoteCollectionCounts;
import com.evernote.edam.notestore.NoteList;
import com.evernote.edam.type.Note;
import com.evernote.edam.type.Notebook;
import com.evernote.edam.type.Tag;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * This class just pumps out some test data on various levels of Evernote: notebooks, tags, ... <br>
 * It is not properly cleaned up yet.
 */
@SuppressWarnings("SameParameterValue")
class TestDataCreator {

    List<Notebook> createTestNotbooks(int numberOfNotebooks) {

        List<Notebook> notebooks = new ArrayList<>() ;

        for (int i = 0; i < numberOfNotebooks ; i++) {
            notebooks.addAll(createTestNotebook()) ;
        }

        return notebooks ;
    }

    List<Notebook> createTestNotebook() {

        Notebook notebook = new Notebook() ;
        notebook.setGuid(UUID.randomUUID().toString());
        notebook.setName(RandomStringUtils.randomAlphanumeric(10));

        List<Notebook> notebooks = new ArrayList<>() ;
        notebooks.add(notebook) ;

        return notebooks ;
    }

    private Tag createTestTag(String name) {

        Tag tag = new Tag() ;
        tag.setGuid(UUID.randomUUID().toString());
        tag.setName(name);

        return tag;
    }

    List<Tag> createTestTags(int numberOfTags) {

        List<Tag> tags = new ArrayList<>() ;

        for (int i = 1; i <= numberOfTags; i++) {
            tags.add(createTestTag("Tag"+i)) ;
        }

        return tags ;
    }

    NoteCollectionCounts createTestNoteCollectionCounts(String uuid) {

        NoteCollectionCounts noteCollectionCounts = new NoteCollectionCounts() ;
        HashMap<String, Integer> noteCollection = new HashMap<>() ;
        noteCollection.put(uuid, 12) ;

        noteCollectionCounts.setNotebookCounts(noteCollection);

        return noteCollectionCounts ;
    }

    NoteList createTestNoteList(int numberOfNotes) {

        NoteList noteList = new NoteList() ;
        List<Note> notes = new ArrayList<>() ;

        for (int i = 0; i < numberOfNotes; i++) {
            notes.add(createTestNote()) ;
        }

        noteList.setNotes(notes);
        return noteList ;
    }

    private Note createTestNote() {

        Note note = new Note() ;
        note.setGuid(UUID.randomUUID().toString());
        note.setContent(RandomStringUtils.randomAlphanumeric(100));

        return note ;
    }
}
