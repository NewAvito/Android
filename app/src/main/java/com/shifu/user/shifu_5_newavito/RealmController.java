package com.shifu.user.shifu_5_newavito;

import android.content.Context;
import android.util.Log;


import com.shifu.user.shifu_5_newavito.model.Author;
import com.shifu.user.shifu_5_newavito.model.Category;
import com.shifu.user.shifu_5_newavito.model.MyRealms;
import com.shifu.user.shifu_5_newavito.model.Product;

import java.lang.reflect.Field;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmModel;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.Sort;

public class RealmController {

    public Realm realm;

    private static RealmController instance = null;
    public static RealmController getInstance() {
        return instance;
    }

    RealmController(Context context){
        if (instance == null) {
            Realm.init(context);
            RealmConfiguration config = new RealmConfiguration.Builder()
                    .deleteRealmIfMigrationNeeded()
                    .build();

            Realm.setDefaultConfiguration(config);
            realm = Realm.getDefaultInstance();
            instance = this;
        }
    }

    /*
     * Create data functions
     */
    public Long newProductId() {
        Long max = (Long) realm.where(Product.class).max(getIdField(Product.class));
        return (max == null)?0:max +1;
    }

    /*
     * Read data functions
     */

    public <T extends RealmObject> Long getSize (Class<T> objClass) {
        if (objClass == null) return null;
        return realm.where(objClass).count();
    }


    public <T extends RealmObject> RealmResults<T> getBase(Class<T> objClass, String sortField){
        RealmResults<T> base;

        boolean sort = exist(objClass, sortField);
        if (sort){
            base = realm.where(objClass).sort(sortField, Sort.DESCENDING).findAll();
        } else {
            base = realm.where(objClass).findAll();
        }

        // Не потокобезопасно! Realms не передаёт свои объекты в другие потоки
        return base;
    }

    public RealmResults<Product> getBaseWithLikes(String sortField){
        RealmResults<Product> base;

        boolean sort = exist(Product.class, sortField);
        if (sort){
            base = realm.where(Product.class).greaterThan("likes", 0).sort(sortField, Sort.DESCENDING).findAll();
        } else {
            base = realm.where(Product.class).greaterThan("likes", 0).findAll();
        }

        // Не потокобезопасно! Realms не передаёт свои объекты в другие потоки
        return base;
    }


    private <T extends RealmObject> boolean exist(Class<T> objClass, String checkField) {
        boolean check = false;
        if (checkField != null)
        {
            for (Field f: objClass.getDeclaredFields()) {
                if (f.getName().equals(checkField)){
                    check = true;
                    break;
                }
            }
        }
        return check;
    }


    // to execute same exceptions in the one place
    public static <T extends RealmModel & MyRealms> String getIdField(Class<T> objClass) {
        String out;
        try {
            out = objClass.newInstance().getIdField();
            return out;
        }catch (IllegalAccessException e){
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        // In Exception case - there isn't such field
        return "";
    }

    // getName частично дублирует функциональность, но, возможно, так удобнее?
    public<T extends RealmModel & MyRealms> T getItem(Class<T> objClass, Long id) {
        T out;
        if (objClass == Author.class) {
            out = realm.where(objClass).findFirst();
        }
        else if (objClass == Product.class && id != null) {
            out = realm.where(objClass).equalTo(getIdField(objClass), id).findFirst();
        }
        else {
            out = null;
        }
        return (out == null) ? null : realm.copyFromRealm(out);
    }

    public String getUsername(){
        Author user = realm.where(Author.class).findFirst();
        return (user == null)?"":user.getUsername();
    }

    /*
     * Update data functions
     */

    public void updateLike(final Long upid, final Long like) {
        realm.executeTransaction(trRealm -> {
            Product item = trRealm.where(Product.class).equalTo(getIdField(Product.class), upid).findFirst();
            Product newItem;
            if (item != null) {
                newItem = trRealm.copyFromRealm(item);
                trRealm.where(Product.class).equalTo(getIdField(Product.class), upid).findFirst().deleteFromRealm();
                newItem.setLikes(like);
                trRealm.copyToRealm(newItem);
            }
        });
    }

    /*
     * Delete data functions
     */
    public<T extends RealmObject> void clear(Class<T> objClass) {
        if (objClass != null) {
            realm.executeTransactionAsync(realm -> realm.where(objClass).findAll().deleteAllFromRealm());
        }
    }

    public void close() {
        if (realm != null) realm.close();
    }
}
