package uwi.dcit.agriexpensestt;


import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.datanucleus.query.JPACursorHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import uwi.dcit.agriexpensestt.EMF;

@Api(name = "translogendpoint", namespace = @ApiNamespace(ownerDomain = "example.com", ownerName = "example.com", packagePath="agriexpensett"))
public class TransLogEndpoint {

  /**
   * This method lists all the entities inserted in datastore.
   * It uses HTTP GET method and paging support.
   *
   * @return A CollectionResponse class containing the list of all entities
   * persisted and a cursor to the next page.
   */
  @SuppressWarnings({"unchecked", "unused"})
  @ApiMethod(name = "listTransLog")
  public CollectionResponse<TransLog> listTransLog(
    @Nullable @Named("cursor") String cursorString,
    @Nullable @Named("limit") Integer limit) {

    EntityManager mgr = null;
    Cursor cursor = null;
    List<TransLog> execute = null;

    try{
      mgr = getEntityManager();
      Query query = mgr.createQuery("select from TransLog as TransLog");
      if (cursorString != null && cursorString != "") {
        cursor = Cursor.fromWebSafeString(cursorString);
        query.setHint(JPACursorHelper.CURSOR_HINT, cursor);
      }

      if (limit != null) {
        query.setFirstResult(0);
        query.setMaxResults(limit);
      }

      execute = (List<TransLog>) query.getResultList();
      cursor = JPACursorHelper.getCursor(execute);
      if (cursor != null) cursorString = cursor.toWebSafeString();

      // Tight loop for fetching all entities from datastore and accomodate
      // for lazy fetch.
      for (TransLog obj : execute);
    } finally {
      mgr.close();
    }

    return CollectionResponse.<TransLog>builder()
      .setItems(execute)
      .setNextPageToken(cursorString)
      .build();
  }

  /**
   * This method gets the entity having primary key id. It uses HTTP GET method.
   *
   * @param id the primary key of the java bean.
   * @return The entity with primary key id.
   */
  @ApiMethod(name="Logs")
  public List<TransLog> Logs(@Named("time") Long tme){
	    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	   
	    com.google.appengine.api.datastore.Query q = new com.google.appengine.api.datastore.Query("TransLog");
	      Filter timeFilter =
	    		  new FilterPredicate("transTime",
	    		                      FilterOperator.GREATER_THAN,
	    		                      33);
	    //  q.setFilter(timeFilter);
	      PreparedQuery pq=datastore.prepare(q);
	      System.out.println("---------I LOVE LIFE 88------------");
	     List<Entity> results = pq
                  .asList(FetchOptions.Builder.withDefaults());
	      Iterator<Entity> i=results.iterator();
	      List<TransLog> tl=new ArrayList<TransLog>();
	      while(i.hasNext()){
	    	  Entity e=i.next();
	    	  System.out.println(" :-"+e.toString());
	    	  System.out.println(":)");
	    	  TransLog tr=new TransLog();
	    	  
	    	  Long id=(Long)e.getProperty("id");
	    	  String s=""+id+"";
	    	  int num=Integer.getInteger(s);
	    	  tr.setId(num);
	    	  
	    	  tr.setKeyrep((String) e.getProperty("keyrep"));
	    	  tr.setOperation((String) e.getProperty("operation"));
	    	  tr.setTableKind((String) e.getProperty("tableKing"));
	    	  
	    	  id=(Long)e.getProperty("rowId");
	    	  s=id+"";
	    	  tr.setRowId(Integer.getInteger(s));
	    	  tl.add(tr);
	      }
		return tl;
  }
	      // Tight loop for fetching all entities from datastore and accomodate
	      // for lazy fetch.
  
  @ApiMethod(name = "getTransLog")
  public TransLog getTransLog(@Named("id") Long id) {
    EntityManager mgr = getEntityManager();
    TransLog translog  = null;
    try {
      translog = mgr.find(TransLog.class, id);
    } finally {
      mgr.close();
    }
    return translog;
  }

  /**
   * This inserts a new entity into App Engine datastore. If the entity already
   * exists in the datastore, an exception is thrown.
   * It uses HTTP POST method.
   *
   * @param translog the entity to be inserted.
   * @return The inserted entity.
   */
  @ApiMethod(name = "insertTransLog")
  public TransLog insertTransLog(TransLog translog) {
    EntityManager mgr = getEntityManager();
    try {
      if(containsTransLog(translog)) {
        throw new EntityExistsException("Object already exists");
      }
      mgr.persist(translog);
    } finally {
      mgr.close();
    }
    return translog;
  }

  /**
   * This method is used for updating an existing entity. If the entity does not
   * exist in the datastore, an exception is thrown.
   * It uses HTTP PUT method.
   *
   * @param translog the entity to be updated.
   * @return The updated entity.
   */
  @ApiMethod(name = "updateTransLog")
  public TransLog updateTransLog(TransLog translog) {
    EntityManager mgr = getEntityManager();
    try {
      if(!containsTransLog(translog)) {
        throw new EntityNotFoundException("Object does not exist");
      }
      mgr.persist(translog);
    } finally {
      mgr.close();
    }
    return translog;
  }

  /**
   * This method removes the entity with primary key id.
   * It uses HTTP DELETE method.
   *
   * @param id the primary key of the entity to be deleted.
   */
  @ApiMethod(name = "removeTransLog")
  public void removeTransLog(@Named("id") Long id) {
    EntityManager mgr = getEntityManager();
    try {
      TransLog translog = mgr.find(TransLog.class, id);
      mgr.remove(translog);
    } finally {
      mgr.close();
    }
  }

  private boolean containsTransLog(TransLog translog) {
    EntityManager mgr = getEntityManager();
    boolean contains = true;
    try {
      TransLog item = mgr.find(TransLog.class, translog.getKey());
      if(item == null) {
        contains = false;
      }
    } finally {
      mgr.close();
    }
    return contains;
  }

  private static EntityManager getEntityManager() {
    return EMF.get().createEntityManager();
  }

}