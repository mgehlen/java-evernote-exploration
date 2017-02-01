package Evernote;

import com.evernote.clients.NoteStoreClient;
import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.notestore.NoteFilter;
import com.evernote.edam.type.Notebook;
import com.evernote.edam.type.Tag;
import com.evernote.thrift.TException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * Is used to test the implementation of the NoteStoreAccessor, which is an Abstraction on Evernote and should deliver to the business code the information packaged in a digestible form.
 */
public class NoteStoreAccessorTest {

    private NotebookAccessor noteBookAccessor;

    private NoteStoreClient noteStore ;
    private final TestDataCreator testData = new TestDataCreator() ;

    @BeforeMethod
    private void mockNoteStore(){
        noteStore = mock(NoteStoreClient.class) ;
    }

    @Test
    public void checkNotebookIsSet() throws TException, EDAMUserException, EDAMSystemException, EDAMNotFoundException {

        List<Notebook> notebooks = testData.createTestNotbooks(3) ;
        when(noteStore.listNotebooks()).thenReturn(notebooks);

        noteBookAccessor = new NotebookAccessor(noteStore,notebooks.get(2).getName());

        verify(noteStore).listNotebooks() ;
        verify(noteStore).getNotebook(notebooks.get(2).getGuid()) ;

    }

    @Test
    public void checkOnlyOneNotebook() throws TException, EDAMUserException, EDAMSystemException, EDAMNotFoundException {


        List<Notebook> notebooks = testData.createTestNotebook() ;
        when(noteStore.listNotebooks()).thenReturn(notebooks);

        noteBookAccessor = new NotebookAccessor(noteStore,notebooks.get(0).getName());

        verify(noteStore).listNotebooks() ;
        verify(noteStore).getNotebook(notebooks.get(0).getGuid()) ;

    }

    @Test(expectedExceptions = EDAMNotFoundException.class)
    public void checkNoteBookNotFound() throws TException, EDAMUserException, EDAMSystemException, EDAMNotFoundException {

        noteBookAccessor = new NotebookAccessor(noteStore,"not_in_here");

        verify(noteStore, times(1)).listNotebooks() ;
        verify(noteStore, times(0)).getNotebook(anyString()) ;
    }

    @Test(dataProvider = "tagListsToFilterFor")
    public void checkFilteringForTags(String filterList, int numberOfTags) throws TException, EDAMUserException, EDAMSystemException, EDAMNotFoundException {

        List<Tag> tags = testData.createTestTags(numberOfTags) ;
        Notebook notebook = setUpNotebookWithTags(tags);
        NoteFilter referenceFilter = setReferenceFilter(notebook.getGuid(), tags) ;

        noteBookAccessor = new NotebookAccessor(noteStore, notebook.getName());
        noteBookAccessor.countNotesWithTags(filterList) ;

        verify(noteStore).findNoteCounts(referenceFilter, false) ;
    }

    @Test(dataProvider = "filtersToShowAll")
    public void checkNoFiltering(String filterList) throws TException, EDAMUserException, EDAMSystemException, EDAMNotFoundException {

        Notebook notebook = setUpNotebook();
        NoteFilter referenceFilter = new NoteFilter();
        referenceFilter.setNotebookGuid(notebook.getGuid());

        noteBookAccessor = new NotebookAccessor(noteStore, notebook.getName());
        noteBookAccessor.countNotesWithTags(filterList) ;

        verify(noteStore, never()).listTags() ;
        verify(noteStore).findNoteCounts(referenceFilter, false) ;
    }

    @Test(dataProvider = "tagListsToFilterFor")
    public void checkNotesRetrievingWithFilter(String filterList, int numberOfTags) throws EDAMUserException, TException, EDAMSystemException, EDAMNotFoundException {

        List<Tag> tags = testData.createTestTags(numberOfTags) ;
        Notebook notebook = setUpNotebookWithTags(tags);
        NoteFilter referenceFilter = setReferenceFilter(notebook.getGuid(), tags) ;

        noteBookAccessor = new NotebookAccessor(noteStore, notebook.getName());
        noteBookAccessor.getNotesWithTags(filterList) ;

        verify(noteStore).findNotes(referenceFilter, 0, 100) ;
    }

    @Test(dataProvider = "filtersToShowAll")
    public void checkNotesRetrievingWithoutFilter(String filterList) throws EDAMUserException, TException, EDAMSystemException, EDAMNotFoundException {

        Notebook notebook = setUpNotebook();
        NoteFilter referenceFilter = new NoteFilter();
        referenceFilter.setNotebookGuid(notebook.getGuid());

        noteBookAccessor = new NotebookAccessor(noteStore, notebook.getName());
        noteBookAccessor.getNotesWithTags(filterList) ;

        verify(noteStore, never()).listTags() ;
        verify(noteStore).findNotes(referenceFilter, 0, 100) ;
    }


    private Notebook setUpNotebookWithTags(List<Tag> tags) throws TException, EDAMUserException, EDAMSystemException, EDAMNotFoundException {

        when(noteStore.listTags()).thenReturn(tags) ;
        return setUpNotebook() ;
    }

    private Notebook setUpNotebook() throws TException, EDAMUserException, EDAMSystemException, EDAMNotFoundException {

        List<Notebook> notebooks = testData.createTestNotebook() ;
        when(noteStore.listNotebooks()).thenReturn(notebooks);
        when(noteStore.getNotebook(anyString())).thenReturn(notebooks.get(0)) ;
        when(noteStore.findNotes(any(NoteFilter.class),anyInt(),anyInt())).thenReturn(testData.createTestNoteList(6)) ;
        when(noteStore.findNoteCounts(any(NoteFilter.class),eq(false))).thenReturn(testData.createTestNoteCollectionCounts(notebooks.get(0).getGuid())) ;

        return notebooks.get(0) ;
    }

    private NoteFilter setReferenceFilter(String notebookGuid, List<Tag> tags) {

        NoteFilter filter = new NoteFilter() ;
        filter.setNotebookGuid(notebookGuid);

        List<String> tagGuids = new ArrayList<>() ;
        for (Tag tag : tags) {
            tagGuids.add(tag.getGuid());
        }
        filter.setTagGuids(tagGuids);

        return filter ;

    }


    @DataProvider
    private static Object[][] tagListsToFilterFor() {

        return new Object[][] {
                {"Tag1,Tag2,Tag3,Tag4,Tag5,Tag6", 6},
                {"Tag1",1},
                {"Tag1,,Tag2",2},
                {"Tag1,",1},
                {",Tag1",1}
        };
    }

    @DataProvider
    private static Object[][] filtersToShowAll() {

        return new Object[][] {
                {""},
                {","}
        };
    }
}
