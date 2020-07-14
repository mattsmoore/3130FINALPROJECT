package com.swg7.dalpolls;

import android.net.Uri;
import android.os.Parcel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.internal.firebase_auth.zzew;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AdditionalUserInfo;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.zzy;
import com.google.firebase.auth.zzz;
import com.swg7.dalpolls.data.IPersistentUser;
import com.swg7.dalpolls.users.AccountCreator;
import com.swg7.dalpolls.users.NewAccountStatus;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

public class CreateAccountTest {

    private AccountCreator accNoDb;

    private final String valid_email = "valid@dal.ca";
    private final String non_dal_email = "someone@notdal.ca";
    private final String garbage_email = "018765rfvbn3krfo9v86$@@@$$t5rqdc345th";

    private final String valid_password = "abcEFG123$%^";
    private final String empty_password = "";
    private final String invalid_password = "a";

    @Before
    public void setUp() {
        accNoDb = new AccountCreator(new FailingPersistentUser());
    }

   /* @Test
    public void testCreateAccount_Created_OnValidCredentials() {
        AccountCreator accSc = new AccountCreator(new AlwaysSucceedingCreationPersistentUser());
        assert accSc.validateNewAccount(valid_email, valid_password) == NewAccountStatus.VALID;
    }*/

    @Test
    public void testCreateAccount_InvalidEmail_OnNonDalEmailDomain() {
        assert accNoDb.validateNewAccount(non_dal_email, valid_password) == NewAccountStatus.INVALID_EMAIL;
    }

    @Test
    public void testCreateAccount_InvalidEmail_OnGarbageEmail() {
        assert accNoDb.validateNewAccount(garbage_email, valid_password) == NewAccountStatus.INVALID_EMAIL;
    }

    @Test
    public void testCreateAccount_InvalidPassword_OnGarbagePassword() {
        assert accNoDb.validateNewAccount(valid_email, empty_password) == NewAccountStatus.INVALID_PASSWORD;
        assert accNoDb.validateNewAccount(valid_email, invalid_password) == NewAccountStatus.INVALID_PASSWORD;
    }

    /*@Test
    public void testCreateAccount_DuplicateEmail_OnDuplicateEmail() {
        AccountCreator accDupDb = new AccountCreator(new AlwaysDuplicatePersistentUser());
        assert accDupDb.validateNewAccount(valid_email, valid_password) == NewAccountStatus.DUPLICATE_EMAIL;
    }*/

    /*@Test
    public void testCreateAccount_DatabaseError_OnBadDatabase() {
        AccountCreator accFailDb = new AccountCreator(new FailingPersistentUser());
        assert accFailDb.validateNewAccount(valid_email, valid_password) == NewAccountStatus.DATABASE_ERROR;
    }*/

    private class FailingPersistentUser implements IPersistentUser {
        public FirebaseUser getCurrentUser() {
            return null;
        }

        public String getUid() {
            return null;
        }

        public Task<AuthResult> createUserWithEmailAndPassword(String email, String password) {
            return null;
        }

        public Task<AuthResult> signInWithEmailAndPassword(String email, String password) {
            return null;
        }

        public void signOut() {
        }

        public Task<SignInMethodQueryResult> fetchSignInMethodsForEmail(String email) {
            return null;
        }
    }

    public class AlwaysDuplicatePersistentUser extends FailingPersistentUser {
        @Override
        public Task<SignInMethodQueryResult> fetchSignInMethodsForEmail(String email) {
            return Tasks.call(new Callable<SignInMethodQueryResult>() {
                public SignInMethodQueryResult call() {
                    return new SignInMethodQueryResult() {
                        public List<String> getSignInMethods() {
                            return Arrays.asList("email", "more", "as long as there's one the user exists.");
                        }
                    };
                }
            });
        }
    }

    private class AlwaysSucceedingCreationPersistentUser extends FailingPersistentUser {
        public FirebaseUser getCurrentUser() {
            return null;
        }

        public String getUid() {
            return null;
        }

        @Override
        public Task<AuthResult> createUserWithEmailAndPassword(String email, String password) {
            return Tasks.call(new Callable<AuthResult>() {
                @Override
                public AuthResult call() throws Exception {
                    return new AuthResult() {
                        @Nullable
                        @Override
                        public FirebaseUser getUser() {
                            return new FirebaseUser() {
                                @NonNull
                                @Override
                                public String getUid() {
                                    return null;
                                }

                                @NonNull
                                @Override
                                public String getProviderId() {
                                    return null;
                                }

                                @Override
                                public boolean isAnonymous() {
                                    return false;
                                }

                                @Nullable
                                @Override
                                public List<String> zza() {
                                    return null;
                                }

                                @NonNull
                                @Override
                                public List<? extends UserInfo> getProviderData() {
                                    return null;
                                }

                                @NonNull
                                @Override
                                public FirebaseUser zza(@NonNull List<? extends UserInfo> list) {
                                    return null;
                                }

                                @Override
                                public FirebaseUser zzb() {
                                    return null;
                                }

                                @NonNull
                                @Override
                                public FirebaseApp zzc() {
                                    return null;
                                }

                                @Nullable
                                @Override
                                public String getDisplayName() {
                                    return null;
                                }

                                @Nullable
                                @Override
                                public Uri getPhotoUrl() {
                                    return null;
                                }

                                @Nullable
                                @Override
                                public String getEmail() {
                                    return null;
                                }

                                @Nullable
                                @Override
                                public String getPhoneNumber() {
                                    return null;
                                }

                                @Nullable
                                @Override
                                public String zzd() {
                                    return null;
                                }

                                @NonNull
                                @Override
                                public zzew zze() {
                                    return null;
                                }

                                @Override
                                public void zza(@NonNull zzew zzew) {

                                }

                                @NonNull
                                @Override
                                public String zzf() {
                                    return null;
                                }

                                @NonNull
                                @Override
                                public String zzg() {
                                    return null;
                                }

                                @Nullable
                                @Override
                                public FirebaseUserMetadata getMetadata() {
                                    return null;
                                }

                                @NonNull
                                @Override
                                public zzz zzh() {
                                    return null;
                                }

                                @Override
                                public void zzb(List<zzy> list) {

                                }

                                @Override
                                public void writeToParcel(Parcel parcel, int i) {

                                }

                                @Override
                                public boolean isEmailVerified() {
                                    return false;
                                }
                            };
                        }

                        @Nullable
                        @Override
                        public AdditionalUserInfo getAdditionalUserInfo() {
                            return null;
                        }

                        @Nullable
                        @Override
                        public AuthCredential getCredential() {
                            return null;
                        }

                        @Override
                        public int describeContents() {
                            return 0;
                        }

                        @Override
                        public void writeToParcel(Parcel parcel, int i) {

                        }
                    };
                }
            });
        }

        public Task<AuthResult> signInWithEmailAndPassword(String email, String password) {
            return null;
        }

        public void signOut() {
        }

        @Override
        public Task<SignInMethodQueryResult> fetchSignInMethodsForEmail(String email) {
            return Tasks.call(new Callable<SignInMethodQueryResult>() {
                public SignInMethodQueryResult call() {
                    return new SignInMethodQueryResult() {
                        public List<String> getSignInMethods() {
                            return new ArrayList<>(); // empty list means new user.
                        }
                    };
                }
            });
        }

    }

}