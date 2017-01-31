package Evernote ;

import com.evernote.auth.EvernoteAuth;
import com.evernote.auth.EvernoteService;
import com.evernote.clients.ClientFactory;
import com.evernote.clients.NoteStoreClient;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.thrift.TException;

import static org.hamcrest.MatcherAssert.assertThat;

final class NoteStoreFetcher {

    private NoteStoreFetcher() {}

    public static NoteStoreClient fetchNoteStore(String authToken) throws TException, EDAMUserException, EDAMSystemException {

        return authenticate(authToken).createNoteStoreClient() ;

    }

    private static ClientFactory authenticate(String authToken) throws TException{

        EvernoteAuth evernoteAuth = new EvernoteAuth(EvernoteService.SANDBOX, authToken);
        ClientFactory factory = new ClientFactory(evernoteAuth);

        failForWrongClientVersion(factory) ;

        return factory ;
    }

    private static void failForWrongClientVersion(ClientFactory clientFactory) throws TException {


            boolean isVersionOk = clientFactory.createUserStoreClient().checkVersion("Exploratory Visualizer",
                    com.evernote.edam.userstore.Constants.EDAM_VERSION_MAJOR,
                    com.evernote.edam.userstore.Constants.EDAM_VERSION_MINOR);

        assertThat("Incompatible Evernote client protocol version", isVersionOk) ;
    }
}