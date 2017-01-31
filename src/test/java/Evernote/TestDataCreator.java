package Evernote;

import com.evernote.edam.notestore.NoteCollectionCounts;
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
class TestDataCreator {

    List<Notebook> createThreeTestNotebooks() {

        List<Notebook> notebooks = new ArrayList<Notebook>() ;

        notebooks.addAll(createTestNotebook()) ;
        notebooks.addAll(createTestNotebook()) ;
        notebooks.addAll(createTestNotebook()) ;

        return notebooks ;
    }

    List<Notebook> createTestNotebook()
    {
        Notebook notebook = new Notebook() ;
        notebook.setGuid(UUID.randomUUID().toString());
        notebook.setName(RandomStringUtils.randomAlphanumeric(10));

        List<Notebook> notebooks = new ArrayList<Notebook>() ;
        notebooks.add(notebook) ;

        return notebooks ;
    }

    private Tag createTestTag(String name) {

        Tag tag = new Tag() ;
        tag.setGuid(UUID.randomUUID().toString());
        tag.setName(name);

        return tag;
    }

    NoteCollectionCounts createTestNoteCollectionCounts(String uuid) {

        NoteCollectionCounts noteCollectionCounts = new NoteCollectionCounts() ;
        HashMap<String, Integer> noteCollection = new HashMap<String, Integer>() ;
        noteCollection.put(uuid, 12) ;

        noteCollectionCounts.setNotebookCounts(noteCollection);

        return noteCollectionCounts ;
    }

    List<Tag> createTestTags(int numberOfTags) {

        List<Tag> tags = new ArrayList<Tag>() ;

        for (int i = 1; i <= numberOfTags; i++) {
            tags.add(createTestTag("Tag"+i)) ;
        }

        return tags ;
    }
}
