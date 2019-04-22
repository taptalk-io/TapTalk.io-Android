package io.taptalk.TapTalk.View.Activity;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.taptalk.TapTalk.API.View.TapDefaultDataView;
import io.taptalk.TapTalk.Helper.OverScrolled.OverScrollDecoratorHelper;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Manager.TAPContactManager;
import io.taptalk.TapTalk.Manager.TAPDataManager;
import io.taptalk.TapTalk.Model.ResponseModel.TAPAddContactByPhoneResponse;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TapTalk.View.Adapter.TAPContactInitialAdapter;
import io.taptalk.TapTalk.View.Adapter.TAPContactListAdapter;
import io.taptalk.TapTalk.ViewModel.TAPContactListViewModel;
import io.taptalk.Taptalk.R;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.CONTACT_LIST;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.PERMISSION_CAMERA_CAMERA;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.PERMISSION_READ_CONTACT;

public class TAPNewChatActivity extends TAPBaseActivity {

    private static final String TAG = TAPNewChatActivity.class.getSimpleName();
    private LinearLayout llButtonNewContact, llButtonScanQR, llButtonNewGroup, llBlockedContacts;
    private ImageView ivButtonClose, ivButtonSearch;
    private TextView tvTitle;
    private RecyclerView rvContactList;
    private NestedScrollView nsvNewChat;

    private TAPContactInitialAdapter adapter;
    private TAPContactListViewModel vm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tap_activity_new_chat);

        initViewModel();
        initView();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.tap_stay, R.anim.tap_slide_down);
    }

    private void permissionCheckAndGetContactList() {
        if (!TAPUtils.getInstance().hasPermissions(this, Manifest.permission.READ_CONTACTS)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, PERMISSION_READ_CONTACT);
        } else {
            getContactList();
        }
    }

    private void initViewModel() {
        vm = ViewModelProviders.of(this).get(TAPContactListViewModel.class);
        //setting up listener for Live Data
        vm.getContactListLive().observe(this, userModels -> {
            if (null != userModels) {
                vm.getContactList().clear();
                vm.getContactList().addAll(userModels);
                vm.setSeparatedContacts(TAPUtils.getInstance().separateContactsByInitial(vm.getContactList()));
                runOnUiThread(() -> adapter.setItems(vm.getSeparatedContacts()));
            }
        });
    }

    private void initView() {
        llButtonNewContact = findViewById(R.id.ll_button_new_contact);
        llButtonScanQR = findViewById(R.id.ll_button_scan_qr);
        llButtonNewGroup = findViewById(R.id.ll_button_new_group);
        llBlockedContacts = findViewById(R.id.ll_blocked_contacts);
        ivButtonClose = findViewById(R.id.iv_button_close);
        ivButtonSearch = findViewById(R.id.iv_button_search);
        tvTitle = findViewById(R.id.tv_title);
        rvContactList = findViewById(R.id.rv_contact_list);
        nsvNewChat = findViewById(R.id.nsv_new_chat);

        getWindow().setBackgroundDrawable(null);

        OverScrollDecoratorHelper.setUpOverScroll(nsvNewChat);

        adapter = new TAPContactInitialAdapter(TAPContactListAdapter.CHAT, vm.getSeparatedContacts());
        rvContactList.setAdapter(adapter);
        rvContactList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvContactList.setHasFixedSize(false);

        // TODO: 21 December 2018 TEMPORARILY DISABLED FEATURE
        llButtonNewGroup.setVisibility(View.GONE);
        llBlockedContacts.setVisibility(View.GONE);

        ivButtonClose.setOnClickListener(v -> onBackPressed());
        ivButtonSearch.setOnClickListener(v -> searchContact());
        llButtonNewContact.setOnClickListener(v -> addNewContact());
        llButtonScanQR.setOnClickListener(v -> openQRScanner());
        llButtonNewGroup.setOnClickListener(v -> createNewGroup());
        llBlockedContacts.setOnClickListener(v -> viewBlockedContacts());

        permissionCheckAndGetContactList();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case PERMISSION_CAMERA_CAMERA:
                    openQRScanner();
                    break;
                case PERMISSION_READ_CONTACT:
                    permissionCheckAndGetContactList();
                    break;
            }
        }
    }

    private void openQRScanner() {
        if (TAPUtils.getInstance().hasPermissions(TAPNewChatActivity.this, Manifest.permission.CAMERA)) {
            Intent intent = new Intent(TAPNewChatActivity.this, TAPBarcodeScannerActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.tap_slide_left, R.anim.tap_stay);
        } else {
            ActivityCompat.requestPermissions(TAPNewChatActivity.this, new String[]{Manifest.permission.CAMERA}, PERMISSION_CAMERA_CAMERA);
        }
    }

    private void searchContact() {
        Intent intent = new Intent(this, TAPSearchContactActivity.class);
        intent.putExtra(CONTACT_LIST, (ArrayList<TAPUserModel>) vm.getContactList());
        startActivity(intent);
        overridePendingTransition(R.anim.tap_slide_left, R.anim.tap_stay);
    }

    private void addNewContact() {
        Intent intent = new Intent(TAPNewChatActivity.this, TAPNewContactActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.tap_slide_left, R.anim.tap_stay);
    }

    private void createNewGroup() {
        Intent intent = new Intent(this, TAPCreateNewGroupActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.tap_slide_left, R.anim.tap_stay);
    }

    private void viewBlockedContacts() {
        Intent intent = new Intent(this, TAPBlockedListActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.tap_slide_left, R.anim.tap_stay);
    }

    private void getContactList() {
        List<String> newContactsPhoneNumbers = new ArrayList<>();
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, ContactsContract.Contacts.DISPLAY_NAME + " COLLATE NOCASE ASC");
        if ((null != cur ? cur.getCount() : 0) > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    if (null != pCur) {
                        while (pCur.moveToNext()) {
                            String phoneNo = pCur.getString(pCur.getColumnIndex(
                                    ContactsContract.CommonDataKinds.Phone.NUMBER));
                            Log.e(TAG, "Name: " + name + " Phone Number: " + phoneNo);
                            String phoneNumb = TAPContactManager.getInstance().convertPhoneNumber(phoneNo);
                            if (!TAPContactManager.getInstance()
                                    .isUserPhoneNumberAlreadyExist(phoneNumb)) {
                                newContactsPhoneNumbers.add(phoneNumb);
                            }
                        }
                        pCur.close();
                    }
                }
            }
        }
        if (cur != null) {
            cur.close();
        }

        TAPDataManager.getInstance().addContactByPhone(newContactsPhoneNumbers, new TapDefaultDataView<TAPAddContactByPhoneResponse>() {
            @Override
            public void onSuccess(TAPAddContactByPhoneResponse response) {
                Log.e(TAG, "onSuccess: "+ response.getUsers().size() );
            }
        });
    }
}
