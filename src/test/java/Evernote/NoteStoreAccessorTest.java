package Evernote;

import com.evernote.clients.NoteStoreClient;
import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.notestore.NoteFilter;
import com.evernote.edam.type.Notebook;
import com.evernote.edam.type.Tag;
import com.evernote.thrift.TException;
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

    private final NoteStoreClient noteStore = mock(NoteStoreClient.class) ;
    private final TestDataCreator testData = new TestDataCreator() ;

    @Test
    public void checkNotebookIsSet() throws TException, EDAMUserException, EDAMSystemException, EDAMNotFoundException {

        List<Notebook> notebooks = testData.createThreeTestNotebooks() ;
        when(noteStore.listNotebooks()).thenReturn(notebooks);

        noteBookAccessor = new NotebookAccessor(noteStore,notebooks.get(2).getName());

        verify(noteStore, times(1)).listNotebooks() ;
        verify(noteStore, times(1)).getNotebook(notebooks.get(2).getGuid()) ;

    }

    @Test
    public void checkOnlyOneNotebook() throws TException, EDAMUserException, EDAMSystemException, EDAMNotFoundException {


        List<Notebook> notebooks = testData.createTestNotebook() ;
        when(noteStore.listNotebooks()).thenReturn(notebooks);

        noteBookAccessor = new NotebookAccessor(noteStore,notebooks.get(0).getName());

        verify(noteStore, times(1)).listNotebooks() ;
        verify(noteStore, times(1)).getNotebook(notebooks.get(0).getGuid()) ;

    }

    @Test(expectedExceptions = EDAMNotFoundException.class)
    public void checkNoteBookNotFound() throws TException, EDAMUserException, EDAMSystemException, EDAMNotFoundException {

        noteBookAccessor = new NotebookAccessor(noteStore,"not_in_here");

        verify(noteStore, times(1)).listNotebooks() ;
        verify(noteStore, times(0)).getNotebook(anyString()) ;
    }

    @Test(dataProvider = "tagListsToFilterFor")
    public void checkFilteringForTags(String filterList, int numberOfTags) throws TException, EDAMUserException, EDAMSystemException, EDAMNotFoundException {

        List<Notebook> notebooks = testData.createTestNotebook() ;
        List<Tag> tags = testData.createTestTags(numberOfTags) ;

        when(noteStore.listNotebooks()).thenReturn(notebooks);
        when(noteStore.getNotebook(anyString())).thenReturn(notebooks.get(0)) ;
        when(noteStore.findNoteCounts(any(NoteFilter.class),eq(false))).thenReturn(testData.createTestNoteCollectionCounts(notebooks.get(0).getGuid())) ;
        when(noteStore.listTags()).thenReturn(tags) ;

        NoteFilter filter = new NoteFilter() ;
        filter.setNotebookGuid(notebooks.get(0).getGuid());

        List<String> tagGuids = new ArrayList<String>() ;
        for (Tag tag : tags) {
            tagGuids.add(tag.getGuid());
        }
        filter.setTagGuids(tagGuids);

        noteBookAccessor = new NotebookAccessor(noteStore, notebooks.get(0).getName());
        noteBookAccessor.countNotesWithTags(filterList) ;

        verify(noteStore, times(1)).findNoteCounts(filter, false) ;
    }

    @Test(dataProvider = "filtersToShowAll")
    public void checkNoFilteringForTags(String filterList) throws TException, EDAMUserException, EDAMSystemException, EDAMNotFoundException {

        List<Notebook> notebooks = testData.createTestNotebook() ;
        when(noteStore.listNotebooks()).thenReturn(notebooks);
        when(noteStore.getNotebook(anyString())).thenReturn(notebooks.get(0)) ;
        when(noteStore.findNoteCounts(any(NoteFilter.class),eq(false))).thenReturn(testData.createTestNoteCollectionCounts(notebooks.get(0).getGuid())) ;

        noteBookAccessor = new NotebookAccessor(noteStore, notebooks.get(0).getName());
        noteBookAccessor.countNotesWithTags(filterList) ;

        NoteFilter filter = new NoteFilter();
        filter.setNotebookGuid(notebooks.get(0).getGuid());

        verify(noteStore, never()).listTags() ;
        verify(noteStore, times(1)).findNoteCounts(filter, false) ;
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
