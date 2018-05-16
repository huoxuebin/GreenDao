package com.example.gdao;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.greendao.gen.DaoMaster;
import com.greendao.gen.DaoSession;
import com.greendao.gen.UserDao;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity {

    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.et_age)
    EditText etAge;
    @BindView(R.id.bt_insert)
    Button btInsert;
    @BindView(R.id.bt_select)
    Button btSelect;
    @BindView(R.id.bt_delete)
    Button btDelete;
    @BindView(R.id.bt_update)
    Button btUpdate;
    @BindView(R.id.rlv)
    RecyclerView rlv;
    @BindView(R.id.et_id)
    EditText etId;
    @BindView(R.id.clder)
    Button clder;
    private UserDao userDao;
    private String name;
    private User user;
    private List<User> users;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(this, "pwk.db", null);
        DaoMaster daoMaster = new DaoMaster(devOpenHelper.getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        userDao = daoSession.getUserDao();
        user = new User();

    }

    @Override
    protected void onResume() {
        super.onResume();
        List<User> users = userDao.loadAll();
        MyAdapter adapter = new MyAdapter(users, this);
        rlv.setLayoutManager(new LinearLayoutManager(MainActivity.this, OrientationHelper.VERTICAL, false));
        MyAdapter myAdapter = new MyAdapter(users, MainActivity.this);
        rlv.setAdapter(myAdapter);
    }

    @OnClick({R.id.bt_insert, R.id.bt_select, R.id.bt_delete, R.id.bt_update})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_insert:
                insert();
                break;
            case R.id.bt_select:
                select();
                break;
            case R.id.bt_delete:
                delete();
                break;
            case R.id.bt_update:
                update();
                break;

        }
    }


    private void insert() {
        name = etName.getText().toString().trim();
        String age = etAge.getText().toString().trim();

        if (name.isEmpty() && age.isEmpty()) {
            Toast.makeText(this, "请输入完整内容", Toast.LENGTH_SHORT).show();
        } else {


            User user = new User(null, name, age);
            long insert = userDao.insert(user);
            Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
            etName.getText().clear();
            etAge.getText().clear();


            users = userDao.loadAll();
            MyAdapter adapter = new MyAdapter(users, this);
            rlv.setLayoutManager(new LinearLayoutManager(MainActivity.this, OrientationHelper.VERTICAL, false));
/*        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rlv.setLayoutManager(linearLayoutManager);*/
            MyAdapter myAdapter = new MyAdapter(users, MainActivity.this);
            rlv.setAdapter(myAdapter);
        }
    }

    private void select() {
        List<User> users = userDao.loadAll();
        MyAdapter adapter = new MyAdapter(users, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rlv.setLayoutManager(linearLayoutManager);
        rlv.setAdapter(adapter);
    }

    private void delete() {
        String id = etId.getText().toString().trim();
        userDao.queryBuilder().where(UserDao.Properties.Id.eq(id)).buildDelete().executeDeleteWithoutDetachingEntities();
        Toast.makeText(this, "删除成功~", Toast.LENGTH_SHORT).show();
        etId.getText().clear();
        select();
    }

    private void update() {

        String id = etId.getText().toString().trim();

        long ids = Long.parseLong(id);



        String s = etName.getText().toString();
        String age = etAge.getText().toString();
        User user = new User(ids, s, age);
        userDao.update(user);

        Toast.makeText(this, "修改成功", Toast.LENGTH_SHORT).show();
        select();

    }


    @OnClick(R.id.clder)
    public void onViewClicked() {

        userDao.deleteAll();
        users.clear();

        users = userDao.loadAll();
        MyAdapter adapter = new MyAdapter(users, this);
        rlv.setLayoutManager(new LinearLayoutManager(MainActivity.this, OrientationHelper.VERTICAL, false));
/*        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rlv.setLayoutManager(linearLayoutManager);*/
        MyAdapter myAdapter = new MyAdapter(users, MainActivity.this);
        rlv.setAdapter(myAdapter);
    }
}
