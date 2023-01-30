package com.rentas.ppob.contact;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rentas.ppob.R;
import com.rentas.ppob.component.ChooserDialog;
import com.rentas.ppob.component.ConfirmDialog;
import com.rentas.ppob.component.HeaderView;
import com.rentas.ppob.component.LoadingDialog;
import com.rentas.ppob.data.CategoryData;
import com.rentas.ppob.database.table.CategoryDB;
import com.rentas.ppob.database.table.ContactDB;
import com.rentas.ppob.master.MyFragment;

import java.util.ArrayList;

public class ContactFragment extends MyFragment {
    private static final int REQ_ADD = 1;

    private FloatingActionButton fab_add;
    private LinearLayout lnly_empty;

    private ContactAdapter mAdapter;
    ArrayList<ContactDB> allContact = new ArrayList<>();
    ArrayList<Bundle> filterContact = new ArrayList<>();

    public static ContactFragment newInstance() {
        Bundle args = new Bundle();
        ContactFragment fragment = new ContactFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int setlayout() {
        return R.layout.contact_fragment_main;
    }

    @Override
    protected void initLayout(View view) {
        lnly_empty = view.findViewById(R.id.lnly_empty);
        fab_add = view.findViewById(R.id.fab_add);

        HeaderView header_view = view.findViewById(R.id.header_view);
        header_view.hideBack();
        header_view.create("Kontak");

        lnly_empty.setVisibility(View.VISIBLE);

        RecyclerView rcvw_data = view.findViewById(R.id.rcvw_data);
        rcvw_data.setLayoutManager(new LinearLayoutManager(mActivity));
        mAdapter = new ContactAdapter(mActivity,filterContact);
        rcvw_data.setAdapter(mAdapter);

        loadData();
    }

    @Override
    protected void initListener() {
        fab_add.setOnClickListener(v -> {
            Intent intent = new Intent(mActivity, AddContactActivity.class);
            startActivityForResult(intent, REQ_ADD);

        });

        mAdapter.setOnSelectedListener(new ContactAdapter.OnSelectedListener() {
            @Override
            public void onSelected(Bundle data, int position) {
                openTransaction(data.getString("customer_id"));
            }

            @Override
            public void onMore(Bundle data, int position) {
                openOption(data);
            }
        });
    }

    private void loadData(){
        allContact.clear();
        ContactDB db = new ContactDB();
        allContact =  db.getData(mActivity);
        if (allContact.size() == 0){
            loadFromFirebase();
        }
        else {
            viewData();
        }

    }

    private void loadFromFirebase(){
        LoadingDialog dialog = new LoadingDialog(mActivity);
        dialog.show();
        ContactFB contactFB = new ContactFB(mActivity);
        contactFB.get(mAgentId+"");
        contactFB.setOnReadListener(data -> {

            for (ContactFB fb : data){
                ContactDB db = new ContactDB();
                db.customerID = fb.customer_id;
                db.name = fb.name;
                db.type = fb.type;
                allContact.add(db);
            }
            ContactDB db = new ContactDB();
            db.insertBulk(mActivity, allContact);
            dialog.dismiss();
            viewData();
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void viewData(){
        filterContact.clear();
        for (ContactDB db : allContact){
            Bundle b = new Bundle();
            b.putString("name", db.name);
            b.putString("customer_id", db.customerID);
            b.putString("type", db.type);
            filterContact.add(b);
        }

        mAdapter.notifyDataSetChanged();
        if (filterContact.size() == 0){
            lnly_empty.setVisibility(View.VISIBLE);
        }
        else {
            lnly_empty.setVisibility(View.GONE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_ADD && resultCode == Activity.RESULT_OK){
            loadData();
        }
    }

    private void openOption(Bundle bundle){
        ChooserDialog dialog = new ChooserDialog(mActivity);
        dialog.add("1","Transaksi");
        dialog.add("2","Edit Kontak");
        dialog.add("3","Hapus Kontak");
        dialog.show(bundle.getString("name")+" ("+bundle.getString("customer_id")+")");
        dialog.setOnSelectedListener(data -> {
            switch (data.getString("key")) {
                case "1":
                    openTransaction(bundle.getString("customer_id"));
                    break;
                case "2":
                    Intent intent = new Intent(mActivity, AddContactActivity.class);
                    intent.putExtra("customer_id", bundle.getString("customer_id"));
                    intent.putExtra("name", bundle.getString("name"));
                    intent.putExtra("type", bundle.getString("type"));
                    startActivityForResult(intent, REQ_ADD);
                    break;
                case "3":
                    delete(bundle);
                    break;
            }
        });
    }

    private void openTransaction(String customerId){
        ChooserDialog dialog = new ChooserDialog(mActivity);
        dialog.add("trans.pulsa.PulsaActivity","Transaksi Pulsa", CategoryData.PULSA);
        dialog.add("trans.paketdata.PaketDataActivity","Transaksi Paket Data",CategoryData.PAKET_DATA);
        dialog.add("trans.pln.token.MainTokenActivity","PLN Token", CategoryData.PLN_TOKEN);
        dialog.add("trans.pln.post.MainPlnPosActivity","PLN Pascabayar",CategoryData.PLN);
        dialog.show("Pilih Transaksi");

        dialog.setOnSelectedListener(data -> {
            String packageName = "com.rentas.ppob.";
            Intent intent = new Intent();
            CategoryDB categoryDB = new CategoryDB();
            categoryDB.getDataByCode(mActivity, data.getString("tag"));

            intent.putExtra("customer_id",customerId);
            intent.putExtra("id", Integer.parseInt(categoryDB.id));
            intent.putExtra("name", categoryDB.name);
            intent.putExtra("code", categoryDB.code);

            intent.setClassName(mActivity,packageName+""+data.getString("key"));
            startActivity(intent);
        });
    }

    private void delete(Bundle data){
        ConfirmDialog dialog = new ConfirmDialog(mActivity);
        dialog.showInfo("Hapus Kontak","Anda akan menghapus kontak "+data.getString("name")+" ("+data.getString("customer_id")+").\n anda yakin?");
        dialog.setAction("Hapus","Batal");
        dialog.setOnCloseLister(action -> {
            if (action == ConfirmDialog.ACTION.YES){
                LoadingDialog loadingDialog = new LoadingDialog(mActivity);
                loadingDialog.show();
                ContactFB contactFB = new ContactFB(mActivity);
                contactFB.customer_id = data.getString("customer_id");
                contactFB.delete();
                contactFB.setDeleteListener(status -> {
                    loadingDialog.dismiss();
                    ContactDB db = new ContactDB();
                    db.delete(mActivity,data.getString("customer_id"));

                    loadData();
                });
            }
        });
    }
}
