package uwi.dcit.AgriExpenseTT.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;

import uwi.dcit.AgriExpenseTT.cloud.CloudInterface;
import uwi.dcit.AgriExpenseTT.models.CycleContract;
import uwi.dcit.AgriExpenseTT.models.CycleResourceContract;
import uwi.dcit.AgriExpenseTT.models.LocalCycle;
import uwi.dcit.AgriExpenseTT.models.LocalCycleUse;
import uwi.dcit.AgriExpenseTT.models.LocalResourcePurchase;
import uwi.dcit.AgriExpenseTT.models.ResourceContract;
import uwi.dcit.AgriExpenseTT.models.ResourcePurchaseContract;
import uwi.dcit.agriexpensesvr.accountApi.model.Account;
import uwi.dcit.agriexpensesvr.resourcePurchaseApi.model.ResourcePurchase;


public class DataManager {
	SQLiteDatabase db;
	DbHelper dbh;
	Context context;
	TransactionLog tL;
	Account acc;
	public DataManager(Context context){
		dbh= new DbHelper(context);
//		db=dbh.getReadableDatabase();
        db = dbh.getWritableDatabase();
		this.context=context;
		tL=new TransactionLog(dbh,db,context);
		acc=DbQuery.getUpAcc(db);
	}
	public DataManager(Context context,SQLiteDatabase db,DbHelper dbh){
		this.dbh= dbh;
		this.db=db;
		this.context=context;
		tL=new TransactionLog(dbh,db,context);
		acc=DbQuery.getUpAcc(db);
	}

	public void insertCycle(int cropId, String landType, double landQty,long time){
		//insert into database
		int id=DbQuery.insertCycle(db, dbh, cropId, landType, landQty,tL,time);
//		if(acc!=null){
//			//insert into transaction table
//			DbQuery.insertRedoLog(db, dbh, CycleContract.CycleEntry.TABLE_NAME, id, "ins");
//			//try insert into cloud
//			if(acc.getSignedIn()==1){
//				CloudInterface c= new CloudInterface(context,db,dbh);// new CloudInterface(context);
//				c.insertCycle();
//			}
//		}
		//update database last updated time
	}

    public int insertCycle(int cropId, String name, String landType, double landQty, long time){
        //insert into database
        int id=DbQuery.insertCycle(db, dbh, cropId, name, landType, landQty,tL,time);
		Log.i("IINNSSEERRTT Cycle", "Account is:"+acc.getAccount());
		CloudInterface c= new CloudInterface(context,db,dbh);// new CloudInterface(context);
        if(acc!=null){
            //insert into transaction table
            DbQuery.insertRedoLog(db, dbh, CycleContract.CycleEntry.TABLE_NAME, id, "ins");
            //try insert into cloud
            if(acc.getSignedIn()==1){
				Log.i("IINNSSEERRTT", "Going to insert into cloud!");
                c.insertCycle();
            }
        }
		time = System.currentTimeMillis()/1000;
		DbQuery.updateAccount(db,time);
		c.updateUpAccC(time);
        return id;
    }


//	public int insertPurchase( int resourceId, String quantifier, double qty, String type, double cost){
//		//insert into database
//		int id=DbQuery.insertResourceExp(db, dbh, type, resourceId, quantifier, qty, cost, tL);
//		Log.i("IINNSSEERRTT Purchase", "Account is:"+acc.getAccount());
//		if(acc!=null){
//			//insert into redo log table
//			DbQuery.insertRedoLog(db, dbh, ResourcePurchaseContract.ResourcePurchaseEntry.TABLE_NAME, id, "ins");
//			//try to insert into cloud
////			if(acc.getSignedIn()==1){
//				CloudInterface c= new CloudInterface(context,db,dbh);//new CloudInterface(context);
//				c.insertPurchase();
////			}
//		}
//		DbQuery.updateAccount(db,System.currentTimeMillis()/1000);
//      return id;
//	}

    public int insertPurchase( int resourceId, String quantifier, double qty,String type, double cost, long time){
        int id = DbQuery.insertResourceExp(db, dbh, type, resourceId, quantifier, qty, cost, time, tL);
		Log.i("IINNSSEERRTT Purchase", "Account is:"+acc.getAccount());
		CloudInterface c= new CloudInterface(context,db,dbh);//new CloudInterface(context);
        if(acc!=null){
            //insert into redo log table
            DbQuery.insertRedoLog(db, dbh, ResourcePurchaseContract.ResourcePurchaseEntry.TABLE_NAME, id, "ins");
            //try to insert into cloud
//            if(acc.getSignedIn()==1){
                c.insertPurchase();
//            }
        }
		time = System.currentTimeMillis()/1000;
		DbQuery.updateAccount(db,time);
		c.updateUpAccC(time);
        return id;
    }
	
	
	
	//------------------------------------------READY TO USE (FROM FRONT)
	public void deleteCycleUse(LocalCycleUse l){
		//update the Purchase that was used (local) add back the amount that was used
		//update cloud, record it in the redo Log purchase Id and the table
		//update the Cycle's total spent (local) subtract the usage cost from the cycle's total spent
		//update cloud, record the update in the redo log
		//finally delete cycleUse (locally)
		//delete cycleUse in cloud, by recording the delete in the redo log
		
//		//PURCHASE
//		//updating local Purchase
//		RPurchase p=DbQuery.getARPurchase(db, dbh, l.getPurchaseId());
//		ContentValues cv=new ContentValues();
//		cv.put(ResourcePurchaseContract.ResourcePurchaseEntry.RESOURCE_PURCHASE_REMAINING, (l.getAmount()+p.getQtyRemaining()) );
//		db.update(ResourcePurchaseContract.ResourcePurchaseEntry.TABLE_NAME, cv, ResourcePurchaseContract.ResourcePurchaseEntry._ID+"="+l.getPurchaseId(), null);
//		//record transaction in log
//		tL.insertTransLog(ResourcePurchaseContract.ResourcePurchaseEntry.TABLE_NAME, l.getPurchaseId(), TransactionLog.TL_DEL);
//		if(acc!=null){
//			//redo log (cloud)
//			DbQuery.insertRedoLog(db, dbh, ResourcePurchaseContract.ResourcePurchaseEntry.TABLE_NAME, l.getPurchaseId(),TransactionLog.TL_UPDATE);
//		}
//		//CYCLE
//		//updating local Cycle
//		Cycle c=DbQuery.getCycle(db, dbh, l.getCycleid());
//		cv=new ContentValues();
//		cv.put(CycleContract.CycleEntry.CROPCYCLE_TOTALSPENT, (c.getTotalSpent()-l.getUseCost()) );
//		db.update(CycleContract.CycleEntry.TABLE_NAME, cv, CycleContract.CycleEntry._ID+"="+l.getCycleid(), null);
//		//record transaction in log
//		tL.insertTransLog(CycleContract.CycleEntry.TABLE_NAME, l.getCycleid(), TransactionLog.TL_UPDATE);
//		if(acc!=null){
//			//redo log (cloud)
//			DbQuery.insertRedoLog(db, dbh, CycleContract.CycleEntry.TABLE_NAME, l.getCycleid(), TransactionLog.TL_UPDATE);
//		}
//		//CYCLEUSE
//		//Delete CycleUse
//		//db.delete(CycleResourceEntry.TABLE_NAME, DbHelper.CYCLE_RESOURCE_ID+"="+l.getId(), null);
//        try {
//            DbQuery.deleteRecord(db, dbh, CycleResourceEntry.TABLE_NAME, l.getId());
//        }catch(Exception e){e.printStackTrace();}
//        tL.insertTransLog(CycleResourceEntry.TABLE_NAME, l.getId(), TransactionLog.TL_DEL);
//		if(acc!=null){
//			//redo log (cloud)
//			DbQuery.insertRedoLog(db, dbh, CycleResourceEntry.TABLE_NAME, l.getId(), TransactionLog.TL_DEL);
//		}
	}
	
	//--------------------------------------READY TO USE (FROM FRONT)
	public void deletePurchase(ResourcePurchase p){
		//get all cycleUse with the purchase's id
		//delete each one using the deleteCycleUse to remove the cost added to each cycle
		//delete the purchase (locally)
		//put the delete in the redo log
		
		//getting all the cycleUse
//		ArrayList<LocalCycleUse> list=new ArrayList<LocalCycleUse>();
//		DbQuery.getCycleUseP(db, dbh, p.getPId(), list, null);
//		Iterator<LocalCycleUse> itr=list.iterator();
//		while(itr.hasNext()){
//			LocalCycleUse l=itr.next();
//			this.deleteCycleUse(l);//already does the recording into the redo log(cloud) and transaction log
//		}
//		//delete purchase
//		db.delete(ResourcePurchaseContract.ResourcePurchaseEntry.TABLE_NAME, ResourcePurchaseContract.ResourcePurchaseEntry._ID+"="+p.getPId(), null);
//		tL.insertTransLog(ResourcePurchaseContract.ResourcePurchaseEntry.TABLE_NAME, p.getPId(), TransactionLog.TL_DEL);
//		if(acc!=null){
//			//redo log (cloud)
//			DbQuery.insertRedoLog(db, dbh, ResourcePurchaseContract.ResourcePurchaseEntry.TABLE_NAME, p.getPId(), TransactionLog.TL_DEL);
//			if(acc.getSignedIn()==1){
//				CloudInterface c= new CloudInterface(context,db,dbh);//new CloudInterface(context);
//				c.deletePurchase();
//			}
//		}
	}
	
	//-----------------------------------READY TO USE (FROM FRONT)
	public void deleteCycle(LocalCycle c){
		//get all cycleUse wih cid
		//delete each one using delete cycleUse as to restore to purchase the amounts used by the cycleUse
		//delete the cycle Locally
		//insert into redo log (cloud)

//		ArrayList<LocalCycleUse> list=new ArrayList<LocalCycleUse>();
//		DbQuery.getCycleUse(db, dbh, c.getId(), list, null);
//		Iterator<LocalCycleUse> itr=list.iterator();
//		while(itr.hasNext()){
//			LocalCycleUse l=itr.next();
//			this.deleteCycleUse(l);//already does the recording into the redo log(cloud) and transaction log
//		}
//		//delete cycle
//		db.delete(CycleContract.CycleEntry.TABLE_NAME, CycleContract.CycleEntry._ID+"="+c.getId(), null);
//        try {
//            DbQuery.deleteRecord(db, dbh, CycleContract.CycleEntry.TABLE_NAME, c.getId());
//        }catch (Exception e){e.printStackTrace();}
//        tL.insertTransLog(CycleContract.CycleEntry.TABLE_NAME, c.getId(), TransactionLog.TL_DEL);
//		if(acc!=null){
//			//insert into redo log (cloud)
//			DbQuery.insertRedoLog(db, dbh, CycleContract.CycleEntry.TABLE_NAME, c.getId(), TransactionLog.TL_DEL);
//			if(acc.getSignedIn()==1){
//				CloudInterface cloud= new CloudInterface(context,db,dbh);//new CloudInterface(context);
//				cloud.deleteCycle();
//			}
//		}
	}
	//---------------------- READY TO USE [WITHOUT INCLUSION OF RESOURCES TABLE]
	public void deleteResource(int rId){
		//get all purchases of rId
		//delete them all using the deletePurchase above (ready to use version)
		//get all cycles who's cycleId is rId
		//delete them all using the deleteCycle above (ready to use version)
		//delete resource and record in transaction log
		//-----Not Sure If we're having resources in the cloud

		ArrayList<LocalResourcePurchase> pList=new ArrayList<LocalResourcePurchase>();
		DbQuery.getResourcePurchases(db, dbh, pList, rId);
		Iterator<LocalResourcePurchase>pI=pList.iterator();
		while(pI.hasNext()){
			this.deletePurchase(pI.next().toRPurchase());
		}
		
		ArrayList<LocalCycle> cList=new ArrayList<LocalCycle>();
		DbQuery.getCycles(db, dbh, cList);
		Iterator<LocalCycle> cI=cList.iterator();
		while(cI.hasNext()){
			LocalCycle c=cI.next();
			if(c.getCropId()==rId)
				this.deleteCycle(c);
		}
		//delete resource
		db.delete(ResourceContract.ResourceEntry.TABLE_NAME, ResourceContract.ResourceEntry._ID+"="+rId, null);
		//not bothering to record in transaction log if not storing resources in clouf
		//not bothering to store in redo log if res not going to cloud
	}
	
	public void insertCycleUse(int cycleId, int resPurchaseId, double qty,String type,String quantifier,double useCost){
		//insert into database
		int id=DbQuery.insertResourceUse(db, dbh, cycleId, type, resPurchaseId, qty,quantifier,useCost, tL);
		//insert into redo log table
		DbQuery.insertRedoLog(db, dbh, CycleResourceContract.CycleResourceEntry.TABLE_NAME, id, "ins");
		//try to insert into cloud
		CloudInterface cloud = new CloudInterface(context, db, dbh);//new CloudInterface(context);
		if(acc!=null ){
//			if(acc.getSignedIn()==1) {
				cloud.insertCycleUseC();
//			}
		}
		long time = System.currentTimeMillis()/1000;
		cloud.updateUpAccC(time);
		DbQuery.updateAccount(db,time);
	}
	
	
	public void updatePurchase(ResourcePurchase p,ContentValues cv){
		db.update(ResourcePurchaseContract.ResourcePurchaseEntry.TABLE_NAME, cv, ResourcePurchaseContract.ResourcePurchaseEntry._ID+"="+p.getPId(),null);
		//update the cloud
		TransactionLog tl=new TransactionLog(dbh, db,context);
		tl.insertTransLog(ResourcePurchaseContract.ResourcePurchaseEntry.TABLE_NAME, p.getPId(), TransactionLog.TL_UPDATE);
		CloudInterface cloud= new CloudInterface(context,db,dbh);// new CloudInterface(context);
		if(acc!=null){
			DbQuery.insertRedoLog(db, dbh, ResourcePurchaseContract.ResourcePurchaseEntry.TABLE_NAME,p.getPId(), TransactionLog.TL_UPDATE);
//			record in transaction log
			if(acc.getSignedIn()==1){
				cloud.updatePurchase();
			}
		}
		long time = System.currentTimeMillis()/1000;
		cloud.updateUpAccC(time);
		DbQuery.updateAccount(db,time);
	}
	public boolean updateCycle(LocalCycle c, ContentValues cv){
		int result = db.update(CycleContract.CycleEntry.TABLE_NAME, cv, CycleContract.CycleEntry._ID+"="+c.getId(), null);
		//update the cloud
		TransactionLog tl = new TransactionLog(dbh, db,context);
		tl.insertTransLog(CycleContract.CycleEntry.TABLE_NAME, c.getId(),TransactionLog.TL_UPDATE);
		CloudInterface cloud= new CloudInterface(context,db,dbh);// new CloudInterface(context);
		if(acc!=null){
			DbQuery.insertRedoLog(db, dbh, CycleContract.CycleEntry.TABLE_NAME, c.getId(), TransactionLog.TL_UPDATE);
			//record in transaction log
			if(acc.getSignedIn()==1){
				cloud.updateCycle();
			}
		}
		long time = System.currentTimeMillis()/1000;
		cloud.updateUpAccC(time);
		DbQuery.updateAccount(db,time);
        return (result != -1);
	}
	
	//------------------------------------------------------------------fixed deletes
	
	
	public void delResource(int resId){
		String code="select * from "+ ResourcePurchaseContract.ResourcePurchaseEntry.TABLE_NAME+" where "
				+ ResourcePurchaseContract.ResourcePurchaseEntry.RESOURCE_PURCHASE_RESID+"="+resId;
		db.delete(ResourceContract.ResourceEntry.TABLE_NAME, ResourceContract.ResourceEntry._ID+"="+resId, null);
		db.delete(CycleContract.CycleEntry.TABLE_NAME, CycleContract.CycleEntry.CROPCYCLE_CROPID+"="+resId, null);
		Cursor cursor=db.rawQuery(code, null);
		if(cursor.getCount()<1)
			return;
		while(cursor.moveToNext()){
			int pId=cursor.getInt(cursor.getColumnIndex(ResourcePurchaseContract.ResourcePurchaseEntry._ID));
			deletePurchase(DbQuery.getARPurchase(db, dbh, pId));
		}
        cursor.close();
	}
	public void insertResource(String name,String type) {
		ContentValues cv=new ContentValues();
		cv.put(ResourceContract.ResourceEntry.RESOURCES_NAME, name);
		cv.put(ResourceContract.ResourceEntry.RESOURCES_TYPE, type);
		db.insert(ResourceContract.ResourceEntry.TABLE_NAME, null, cv);
	}
}
