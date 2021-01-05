package com.v.oldcall.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.RawContacts.Data;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.v.oldcall.R;
import com.v.oldcall.entities.ContactEntity;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class ContactUtil {
    // 号码
    private final static String NUM = ContactsContract.CommonDataKinds.Phone.NUMBER;
    // 联系人姓名
    private final static String NAME = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME;
    // 头像ID
    private final static String PHOTO_ID = ContactsContract.Contacts.Photo.PHOTO_ID;
    // 联系人ID
    private final static String RAW_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID;

    // 联系人提供者的uri
    private final static Uri phoneUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
    private final static Uri CONTENT_URI = ContactsContract.Data.CONTENT_URI;
    private final static Uri contact_content_uri = ContactsContract.Contacts.CONTENT_URI;
    private final static Uri raw_content_uri = ContactsContract.RawContacts.CONTENT_URI;

    // 获取所有联系人
    public static List<ContactEntity> getPhone(Context context) {

        List<ContactEntity> mListDetail = new ArrayList<ContactEntity>();
        ContentResolver cr = context.getContentResolver();

        Cursor cursor = cr.query(phoneUri, new String[]{NUM, NAME, PHOTO_ID, RAW_CONTACT_ID}, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                // 得到手机号码
                String phoneNumber = cursor.getString(cursor.getColumnIndex(NUM));
                // 当手机号码为空的或者为空字段 跳过当前循环
                if (TextUtils.isEmpty(phoneNumber))
                    continue;

                // 得到联系人名称
                String contactName = cursor.getString(cursor.getColumnIndex(NAME));
                // 得到联系人ID
                Long contactid = cursor.getLong(cursor.getColumnIndex(RAW_CONTACT_ID));
                // 得到联系人头像ID
                Long photoId = cursor.getLong(cursor.getColumnIndex(PHOTO_ID));
                // 得到联系人头像Bitamp
                Bitmap bitmap = null;
                Log.i("ContactUtil", "photoId=" + photoId + ",contactid=" + contactid + ",contactName=" + contactName
                        + ",phoneNumber=" + phoneNumber);
                ContactEntity phone = new ContactEntity();
                // photoid 大于0 表示联系人有头像 如果没有给此人设置头像则给他一个默认的
                if (photoId > 0) {
                    Uri uri = ContentUris.withAppendedId(contact_content_uri, contactid);
                    InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(cr, uri);
                    bitmap = BitmapFactory.decodeStream(input);
                    phone.setAvatar(uri.toString());
                }

                phone.setCid(contactid);
                phone.setPid(photoId);
                phone.setName(contactName);
                phone.setPhone(phoneNumber);
                mListDetail.add(phone);
            }
            cursor.close();
        }
        return mListDetail;
    }

    // 获取联系人
    public static ContactEntity getContactEntityById(Context context, long raw_contact_id) {
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query(phoneUri, new String[]{NUM, NAME, PHOTO_ID}, RAW_CONTACT_ID + "=" + raw_contact_id, null,
                null);
        ContactEntity phone = null;
        if (cursor != null && cursor.moveToNext()) {
            // 得到手机号码
            String phoneNumber = cursor.getString(cursor.getColumnIndex(NUM));
            // 当手机号码为空的或者为空字段 跳过当前循环
            if (TextUtils.isEmpty(phoneNumber)) {
                cursor.close();
                return null;
            }

            // 得到联系人名称
            String contactName = cursor.getString(cursor.getColumnIndex(NAME));
            // 得到联系人头像ID
            Long photoId = cursor.getLong(cursor.getColumnIndex(PHOTO_ID));
            // 得到联系人头像Bitamp
            Bitmap bitmap = null;
            Log.i("ContactUtil", "ContactEntity,photoId=" + photoId + ",raw_contact_id=" + raw_contact_id + ",contactName="
                    + contactName + ",phoneNumber=" + phoneNumber);
            phone = new ContactEntity();
            // photoid 大于0 表示联系人有头像 如果没有给此人设置头像则给他一个默认的
            if (photoId > 0) {
                Uri uri = ContentUris.withAppendedId(contact_content_uri, raw_contact_id);
                InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(cr, uri);
                bitmap = BitmapFactory.decodeStream(input);
                phone.setAvatar(uri.toString());
            }

            phone.setCid(raw_contact_id);
            phone.setPid(photoId);
            phone.setName(contactName);
            phone.setPhone(phoneNumber);
            cursor.close();
        }
        return phone;
    }

    // 一个添加联系人信息的例子
    public static void addContact(Context context, ContactEntity phoneDetail) {
        // 创建一个空的ContentValues
        ContentValues values = new ContentValues();
        ContentResolver cResolver = context.getContentResolver();
        // 向RawContacts.CONTENT_URI空值插入，
        // 先获取Android系统返回的rawContactId
        // 后面要基于此id插入值
        long rawContactId = ContentUris.parseId(cResolver.insert(RawContacts.CONTENT_URI, values));

        values.put(Data.RAW_CONTACT_ID, rawContactId);
        // 内容类型
        values.put(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);
        // 联系人名字
        values.put(StructuredName.DISPLAY_NAME, phoneDetail.getName());
        // 向联系人URI添加联系人名字
        cResolver.insert(CONTENT_URI, values);
        values.clear();

        values.put(Data.RAW_CONTACT_ID, rawContactId);
        values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
        // 联系人的电话号码
        values.put(Phone.NUMBER, phoneDetail.getPhone());
        // 电话类型
        values.put(Phone.TYPE, Phone.TYPE_MOBILE);
        // 向联系人电话号码URI添加电话号码
        cResolver.insert(CONTENT_URI, values);
        values.clear();

        if (phoneDetail.getPid() > 0) {
            // 修改联系人的头像
            Uri uri = ContentUris.withAppendedId(contact_content_uri, rawContactId);
            InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(cResolver, uri);
            Bitmap sourceBitmap = BitmapFactory.decodeStream(input);
            if (sourceBitmap == null) return;
            final ByteArrayOutputStream os = new ByteArrayOutputStream();
            // 将Bitmap压缩成PNG编码，质量为100%存储
            sourceBitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
            byte[] avatar = os.toByteArray();
            values.put(Data.RAW_CONTACT_ID, rawContactId);
            values.put(Data.MIMETYPE, Photo.CONTENT_ITEM_TYPE);
            values.put(Photo.PHOTO, avatar);
            cResolver.insert(CONTENT_URI, values);
        }

//        Toast.makeText(context, R.string.savingContact, Toast.LENGTH_SHORT).show();
    }

    // 一个添加联系人信息的例子
    public static void updateContact(Context context, ContactEntity phoneDetail) {
        // 创建一个空的ContentValues
        ContentValues values = new ContentValues();
        ContentResolver cResolver = context.getContentResolver();
        long rawContactId = phoneDetail.getId();
        // 更新电话号码
        values.put(Phone.NUMBER, phoneDetail.getPhone());
        int numbre = cResolver.update(CONTENT_URI, values, Data.MIMETYPE + "=? and raw_contact_id= ?",
                new String[]{Phone.CONTENT_ITEM_TYPE, rawContactId + ""});
        // 更新联系人姓名
        values.clear();
        values.put(StructuredName.DISPLAY_NAME, phoneDetail.getName());
        int NAMES = cResolver.update(CONTENT_URI, values, Data.MIMETYPE + "=? and raw_contact_id = ?",
                new String[]{StructuredName.CONTENT_ITEM_TYPE, rawContactId + ""});
        values.clear();

        Long photoId = phoneDetail.getPid();
//        Bitmap sourceBitmap = phoneDetail.getPhonePhoto();// BitmapFactory.decodeResource(getResources(),R.drawable.img2);
        Bitmap sourceBitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.pic_default_avatar);
        Log.e("ContactUtil", "numbre=" + numbre + ",NAME=" + NAMES + ",PhonePhoto=" + sourceBitmap + ",photoId=" + photoId);
        if (sourceBitmap == null) return;
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        // 将Bitmap压缩成PNG编码，质量为100%存储
        sourceBitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
        byte[] avatar = os.toByteArray();
        // 更新
        if (photoId > 0) {
            values.put(Photo.PHOTO, avatar);
            int id = cResolver.update(CONTENT_URI, values,
                    Data.MIMETYPE + "=? AND " + Data.RAW_CONTACT_ID + "= " + rawContactId,
                    new String[]{Photo.CONTENT_ITEM_TYPE});
        } else {
            // 插入,保存联系人头像
            values.put(Data.RAW_CONTACT_ID, rawContactId);
            values.put(Data.MIMETYPE, Photo.CONTENT_ITEM_TYPE);
            values.put(Photo.PHOTO, avatar);
            cResolver.insert(CONTENT_URI, values);
        }

//        Toast.makeText(context, R.string.savingContact, Toast.LENGTH_SHORT).show();
    }

    // 删除联系人
    public static void deleteContact(Activity activity, long rawContactId) {

        // 根据姓名求id
        ContentResolver resolver = activity.getContentResolver();
        resolver.delete(raw_content_uri, Data._ID + " = " + rawContactId, null);
        resolver.delete(CONTENT_URI, "raw_contact_id=" + rawContactId, null);
//        Toast.makeText(activity, R.string.contacts_deleted_toast, Toast.LENGTH_SHORT).show();

    }

}

 