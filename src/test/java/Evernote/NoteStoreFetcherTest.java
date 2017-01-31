package Evernote;

import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.thrift.TException;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * write tests for the NoteStoreFetcher. This one is used to see if I can really connect to Evernote API and thusly connects to a sandbox.
 * It is used to ensure that I still can fetch notebookStores as I think they work, since I mock them in most of the other tests.
 */
public class NoteStoreFetcherTest {

    private static final String USER_TOKEN = "S=s1:U=933a0:E=1609de4739a:C=15946334410:P=1cd:A=en-devtoken:V=2:H=8ea58969963f4a6dd3c3d63bc9380993";
    private static final String USER_DOES_NOT_EXISTS = "S=s1:U=933a0:E=1609de4739a:C=15946334410:P=1cd:A=en-devtoken:V=2:H=8ea58969963f4a6dd3c3d63bc9380991";

    @Test(expectedExceptions = EDAMUserException.class)
    public void checkInvalidUserToken() throws TException, EDAMUserException, EDAMSystemException {
        NoteStoreFetcher.fetchNoteStore("INVALID_USER") ;
    }

    @Test(expectedExceptions = EDAMSystemException.class)
    public void checkWrongUserToken() throws TException, EDAMUserException, EDAMSystemException {
        NoteStoreFetcher.fetchNoteStore(USER_DOES_NOT_EXISTS) ;
    }

    @Test
    public void checkNotbookFound() throws TException, EDAMUserException, EDAMSystemException {

        String defaultNotebookName = NoteStoreFetcher.fetchNoteStore(USER_TOKEN).getDefaultNotebook().getName() ;
        assertThat("No Default Notebook, hence no Store", defaultNotebookName, is("Erstes Notizbuch")) ;
    }

}
