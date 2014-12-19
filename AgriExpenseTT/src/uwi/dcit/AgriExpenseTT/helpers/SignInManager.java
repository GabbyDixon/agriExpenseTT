package uwi.dcit.AgriExpenseTT.helpers;

import java.util.ArrayList;

import uwi.dcit.AgriExpenseTT.MainMenu;
import uwi.dcit.AgriExpenseTT.models.UpdateAccountContract.UpdateAccountEntry;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.agriexpensett.upaccendpoint.model.UpAcc;

public class SignInManager {
	Context context;
	SQLiteDatabase db;
	DbHelper dbh;
	Activity activity;
	
	public SignInManager(Activity activity, Context ctx){
		this.context = ctx;
		this.activity = activity;
		dbh = new DbHelper(context);
		db = dbh.getReadableDatabase();
	}
	
	public SignInManager(SQLiteDatabase db,DbHelper dbh,Activity activity,Context ctx){
		this.context = ctx;
		this.db = db;
		this.dbh = dbh;
		this.activity = activity;
	}
	
	public void signIn(){
		UpAcc acc = isExisting(); 								// Check if Account is already created
		if(acc == null)accountSetUp();							// Account doesn't exist so we've to setup a new one (means we never signed in)
		else{													// account exists already so we can sign in OR out
			Log.d("SignIn Manager", "Account Exists");
			if(acc.getSignedIn() == 1){							// we're already signed in so lets sign out
				Log.d("SignIn Manager", "Account Signed in Attempting to sign out");
				// updates the database that we signed out
				ContentValues cv = new ContentValues();	
				cv.put(UpdateAccountEntry.UPDATE_ACCOUNT_SIGNEDIN, 0);	
				db.update(UpdateAccountEntry.TABLE_NAME, cv, UpdateAccountEntry._ID+"=1",null); 
			}else{												// if we're signed out then we need to sign in 
				Log.d("SignIn Manager", "Account previously created attempting to signin with namespace: "+acc.getAcc());
				initialSignIn(acc.getAcc());					// Initiate Sign-in process
			}
		}
	}
	
	public boolean signOut(){
		Log.d("SignIn Manager", "Account is logged in, attempting to sign out");
		ContentValues cv = new ContentValues();	
		cv.put(UpdateAccountEntry.UPDATE_ACCOUNT_SIGNEDIN, 0);	
		db.update(UpdateAccountEntry.TABLE_NAME, cv, UpdateAccountEntry._ID + "=1",null); 
		return true;
	}
	
	private void initialSignIn(final String namespace){
		new SetupSignInTask(namespace).execute(); //The SetupSignInTask will handle the process of signing in within a new task/thread
	}
	
	public void accountSetUp(){
		ArrayList<String> deviceAccounts = getAccounts();		
		if(deviceAccounts.isEmpty()){
			handleNoAccounts();
			return;
		}
			
		final CharSequence[] items = new CharSequence[deviceAccounts.size()]; 
		int i=0;
		
		for(String k : deviceAccounts){
//			items[i]=deviceAccounts.get(i++); 
			items[i++] = k; //TODO Ensure that this works as expected
			Log.d("SignIn Manager", "k => "+ k +", get => "+ deviceAccounts.get(i - 1));
		}
		//Display List to user for choosing account
	    (new AlertDialog.Builder(context))
	    	.setTitle("Select Account")
	    	.setItems(items, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {
		            initialSignIn(convertString2Namespace((items[item].toString()))); //convert choice to name space and attempt to upload
		        }
		    })
		    .show();
	}
	
	public void signInReturn(boolean success,String message){
		if(success){
			((MainMenu)activity).toggleSignIn();
			Toast.makeText(activity.getBaseContext(), "Sign-in Successfully Completed", Toast.LENGTH_SHORT).show();
		}else{
			if(message==null||message.equals(""))
				Toast.makeText(activity, "The sign-in process was not successful. Please try again", Toast.LENGTH_SHORT).show();
			else 
				Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
		}
	}
	
	//--------------------------------------------------Helper stuff
	private void handleNoAccounts(){		
		(new AlertDialog.Builder(context))
			.setTitle("No Accounts Available")
			.setMessage("A Google Account is required to backup the application data. Create an account before attempting to backup")
			.setNeutralButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {
					context.startActivity(new Intent(context,MainMenu.class));
				}
			})
			.setNeutralButton("Create Account", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {
					context.startActivity(new Intent(android.provider.Settings.ACTION_ADD_ACCOUNT));
				}
			})
			.show();
	}
	
	private ArrayList<String> getAccounts(){
		ArrayList<String> accountList = new ArrayList<String>();
		Account[] accounts = AccountManager.get(context).getAccounts();
		for(Account a:accounts){
		  accountList.add(a.name);
		}
		return accountList;
	}
	
	private String convertString2Namespace(String str){
		int len = str.length();
		String newStr = "_";
		for(int i = 0; i < len; i++){
			if(isChar(str.charAt(i))) //TODO Why only characters?
				newStr += str.charAt(i);
			else if(str.charAt(i) == '@')
				break;
		}
		return newStr;
	}
	
	private boolean isChar(char c){
		if((c>='a'&&c<='z')||(c>='A'&&c<='Z'))
			return true;
		return false;
	}
	
	public UpAcc isExisting(){
		UpAcc acc = DbQuery.getUpAcc(db); //Attempts to retrieve the Account from the database Record		
		if(acc.getAcc() == null || acc.getAcc().equals(""))return null; //The information returned will be null if no record exists
		Log.d("SignIn Manager","Account already Created ... Returning existing Account");
		return acc; //Return the Account if received.
	}
	
	public boolean isSignedIn(){
		UpAcc account = this.isExisting();
		if (account == null)return false; 		// No account created
		return (account.getSignedIn() == 1); 	// Account create 1 of logged in 0 if logged out
	}
	
	public SignInManager getSignin(){
		return this;
	}
	
	//Create an Asynchronous Task to create new thread to handle this process
	class SetupSignInTask extends AsyncTask<Void, Void, UpAcc>{
		private String namespace;
		
		public SetupSignInTask(String namespace){
			this.namespace=namespace;
		}
		@Override
		protected UpAcc doInBackground(Void... params) {
			CloudInterface cloudIF = new CloudInterface(context, db, dbh);
			UpAcc cloudAcc=cloudIF.getUpAcc(namespace);//getting a the cloud upAcc if there's any >.<
			return cloudAcc;//this is passed to onPostExecute
		}

		@Override
		protected void onPostExecute(UpAcc cloudAcc) {
			Sync sync=new Sync(db, dbh, context,getSignin());
			sync.start(namespace, cloudAcc);
			super.onPostExecute(cloudAcc);
		}
		
	}
}	