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
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

/**
 * Created by marcelg on 31/12/16 as Appium PoC.
 */
public class NoteStoreAccessorTest {

    NoteStoreClient noteStore ;
    NotebookAccessor noteBookAccessor;

    @Before
    public void setupNotestoreMock() throws TException, EDAMUserException, EDAMSystemException, EDAMNotFoundException{

        noteStore = mock(NoteStoreClient.class) ;

    }

    @Test
    public void checkNotebookIsSet() throws TException, EDAMUserException, EDAMSystemException, EDAMNotFoundException {

        List<Notebook> notebooks = createThreeTestNotebooks() ;
        when(noteStore.listNotebooks()).thenReturn(notebooks);

        noteBookAccessor = new NotebookAccessor(noteStore,notebooks.get(2).getName());

        verify(noteStore, times(1)).listNotebooks() ;
        verify(noteStore, times(1)).getNotebook(notebooks.get(2).getGuid()) ;

    }

    @Test
    public void checkOnlyOneNotebook() throws TException, EDAMUserException, EDAMSystemException, EDAMNotFoundException {


        List<Notebook> notebooks = createTestNotebook() ;
        when(noteStore.listNotebooks()).thenReturn(notebooks);

        noteBookAccessor = new NotebookAccessor(noteStore,notebooks.get(0).getName());

        verify(noteStore, times(1)).listNotebooks() ;
        verify(noteStore, times(1)).getNotebook(notebooks.get(0).getGuid()) ;

    }

    @Test(expected = EDAMNotFoundException.class)
    public void checkNoteBookNotFound() throws TException, EDAMUserException, EDAMSystemException, EDAMNotFoundException {

        noteBookAccessor = new NotebookAccessor(noteStore,"not_in_here");

        verify(noteStore, times(1)).listNotebooks() ;
        verify(noteStore, times(0)).getNotebook(anyString()) ;
    }

    @Test
    public void checkEmptyTagListListsAllNotes() throws EDAMUserException, TException, EDAMSystemException, EDAMNotFoundException {

        List<Notebook> notebooks = createTestNotebook() ;
        when(noteStore.listNotebooks()).thenReturn(notebooks);
        when(noteStore.getNotebook(anyString())).thenReturn(notebooks.get(0)) ;
        when(noteStore.findNoteCounts(any(NoteFilter.class),eq(false))).thenReturn(createTestNoteCollectionCounts(notebooks.get(0).getGuid())) ;

        noteBookAccessor = new NotebookAccessor(noteStore, notebooks.get(0).getName());
        noteBookAccessor.countNotesWithTags("",",", false) ;

        when(noteStore.getTag(anyString())).thenReturn(createTestTag("Tag1")) ;

        verify(noteStore, never()).listTags() ;
        verify(noteStore, times(1)).findNoteCounts(any(NoteFilter.class), eq(false)) ;
    }

    @Test
    public void checkSearchWithOneTag() throws EDAMUserException, TException, EDAMSystemException, EDAMNotFoundException {

        List<Notebook> notebooks = createTestNotebook() ;
        when(noteStore.listNotebooks()).thenReturn(notebooks);

        noteBookAccessor = new NotebookAccessor(noteStore, notebooks.get(0).getName());
        noteBookAccessor.countNotesWithTags("Tag1",",", false) ;
        when(noteStore.getTag(anyString())).thenReturn(createTestTag("Tag1")) ;

        verify(noteStore, times(1)).listTags() ;
        verify(noteStore, times(1)).getTag(anyString()) ;
        verify(noteStore, times(1)).findNoteCounts(any(NoteFilter.class), eq(false)) ;
    }

    @Test
    public void checkSearchWithManyTags() throws EDAMUserException, TException, EDAMSystemException, EDAMNotFoundException {
  /*      List<Notebook> notebooks = createTestNotebook() ;
        List<Tag> tags = createTestTags(6) ;

        when(noteStore.listNotebooks()).thenReturn(notebooks);
        when(noteStore.getNotebook(anyString())).thenReturn(notebooks.get(0)) ;
        when(noteStore.findNoteCounts(any(NoteFilter.class),eq(false))).thenReturn(createTestNoteCollectionCounts(notebooks.get(0).getGuid())) ;
        when(noteStore.listTags()).thenReturn(tags) ;

        NoteFilter filter = new NoteFilter() ;
        filter.setNotebookGuid(notebooks.get(0).getGuid());

        List<String> tagGuids = new ArrayList<String>() ;
        for (int i = 0; i < 6; i++) {
            tagGuids.add(tags.get(i).getGuid()) ;
        }
        filter.setTagGuids(tagGuids);

        noteBookAccessor = new NotebookAccessor(noteStore, notebooks.get(0).getName());
        noteBookAccessor.countNotesWithTags("Tag1,Tag2,Tag3,Tag4,Tag5,Tag6",",", false) ;

        verify(noteStore, times(1)).findNoteCounts(filter, false) ;*/

        checkProperly("Tag1,Tag2,Tag3,Tag4,Tag5,Tag6",",", 6);
    }

    private void checkProperly(String filterList, String delimiter, int number) throws TException, EDAMUserException, EDAMSystemException, EDAMNotFoundException {
        List<Notebook> notebooks = createTestNotebook() ;
        List<Tag> tags = createTestTags(number) ;

        when(noteStore.listNotebooks()).thenReturn(notebooks);
        when(noteStore.getNotebook(anyString())).thenReturn(notebooks.get(0)) ;
        when(noteStore.findNoteCounts(any(NoteFilter.class),eq(false))).thenReturn(createTestNoteCollectionCounts(notebooks.get(0).getGuid())) ;
        when(noteStore.listTags()).thenReturn(tags) ;

        NoteFilter filter = new NoteFilter() ;
        filter.setNotebookGuid(notebooks.get(0).getGuid());

        List<String> tagGuids = new ArrayList<String>() ;
        for (int i = 0; i < tags.size(); i++) {
            tagGuids.add(tags.get(i).getGuid()) ;
        }
        filter.setTagGuids(tagGuids);

        noteBookAccessor = new NotebookAccessor(noteStore, notebooks.get(0).getName());
        noteBookAccessor.countNotesWithTags(filterList,delimiter, false) ;

        verify(noteStore, times(1)).findNoteCounts(filter, false) ;
    }

    @Test
    public void checkNoTagsBetweenDelimiterIsIgnored() throws EDAMUserException, TException, EDAMSystemException, EDAMNotFoundException {

        List<Notebook> notebooks = createTestNotebook() ;
        when(noteStore.listNotebooks()).thenReturn(notebooks);

        noteBookAccessor = new NotebookAccessor(noteStore, notebooks.get(0).getName());
        noteBookAccessor.countNotesWithTags("Tag1,,Tag2",",", false) ;

        verify(noteStore, times(1)).listTags() ;
        verify(noteStore, times(2)).getTag(anyString()) ;
        verify(noteStore, times(1)).findNoteCounts(any(NoteFilter.class), eq(false)) ;
    }

    @Test (expected = EDAMUserException.class)
    public void checkNoNewTagAfterDelimiterIsIgnored() throws EDAMUserException, TException, EDAMSystemException, EDAMNotFoundException {

        List<Notebook> notebooks = createTestNotebook() ;
        when(noteStore.listNotebooks()).thenReturn(notebooks);

        noteBookAccessor = new NotebookAccessor(noteStore, notebooks.get(0).getName());
        noteBookAccessor.countNotesWithTags("Tag1,",",", false) ;

        verify(noteStore, times(1)).listTags() ;
        verify(noteStore, times(1)).getTag(anyString()) ;
        verify(noteStore, times(1)).findNoteCounts(any(NoteFilter.class), eq(false)) ;
    }

    @Test
    public void checkDelimiterOnStartIsIgnored() throws EDAMUserException, TException, EDAMSystemException, EDAMNotFoundException {

        List<Notebook> notebooks = createTestNotebook() ;
        List<Tag> tags = createTestTags(1) ;

        when(noteStore.listNotebooks()).thenReturn(notebooks);
        when(noteStore.getNotebook(anyString())).thenReturn(notebooks.get(0)) ;
        when(noteStore.findNoteCounts(any(NoteFilter.class),eq(false))).thenReturn(createTestNoteCollectionCounts(notebooks.get(0).getGuid())) ;
        when(noteStore.listTags()).thenReturn(tags) ;

        NoteFilter filter = new NoteFilter() ;
        filter.setNotebookGuid(notebooks.get(0).getGuid());

        List<String> tagGuids = new ArrayList<String>() ;
        tagGuids.add(tags.get(0).getGuid()) ;
        filter.setTagGuids(tagGuids);

        noteBookAccessor = new NotebookAccessor(noteStore, notebooks.get(0).getName());
        noteBookAccessor.countNotesWithTags(",Tag1",",", false) ;


        verify(noteStore, times(1)).findNoteCounts(filter, false) ;
    }

    @Test
    public void checkOnlyDelimiterListsAll() throws EDAMUserException, TException, EDAMSystemException, EDAMNotFoundException {

        List<Notebook> notebooks = createTestNotebook() ;
        when(noteStore.listNotebooks()).thenReturn(notebooks);
        when(noteStore.getNotebook(anyString())).thenReturn(notebooks.get(0)) ;
        when(noteStore.findNoteCounts(any(NoteFilter.class),eq(false))).thenReturn(createTestNoteCollectionCounts(notebooks.get(0).getGuid())) ;

        noteBookAccessor = new NotebookAccessor(noteStore, notebooks.get(0).getName());
        noteBookAccessor.countNotesWithTags(",",",", false) ;

        verify(noteStore, times(0)).listTags() ;
        verify(noteStore, times(1)).findNoteCounts(any(NoteFilter.class), eq(false)) ;
    }

    private List<Notebook> createThreeTestNotebooks() {

        List<Notebook> notebooks = new ArrayList<Notebook>() ;

        notebooks.addAll(createTestNotebook()) ;
        notebooks.addAll(createTestNotebook()) ;
        notebooks.addAll(createTestNotebook()) ;

        return notebooks ;
    }

    private List<Notebook> createTestNotebook()
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

    private NoteCollectionCounts createTestNoteCollectionCounts(String uuid) {

        NoteCollectionCounts noteCollectionCounts = new NoteCollectionCounts() ;
        HashMap<String, Integer> noteCollection = new HashMap<String, Integer>() ;
        noteCollection.put(uuid, 12) ;

        noteCollectionCounts.setNotebookCounts(noteCollection);

        return noteCollectionCounts ;
    }

    private List<Tag> createTestTags(int numberOfTags) {

        List<Tag> tags = new ArrayList<Tag>() ;

        for (int i = 1; i <= numberOfTags; i++) {
            tags.add(createTestTag("Tag"+i)) ;
        }

        return tags ;
    }
}
