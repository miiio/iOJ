package com.ioj.wax.ioj;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


public class MainActivity extends AppCompatActivity {
    private DrawerLayout dl;
    private long exitTime = 0;
    private final static int Login_REQUEST_CODE=1;
    private HomeFragment mHomeFragment;
    private RanklistFragment mRanklistFragment;
    private StatusFragment mStatusFragment;
    private ProblemsFragment mProblemsFragment;
    private ContestFragment mContestFragment;
    private NavigationView mNavigationView;
    private ImageView btn_login;
    private TextView TextView__username;
    private TextView TextView_maxin;
    private Bitmap pic_bitmap;
    public Toolbar mToolbar;
    public ActionBarDrawerToggle mDrawerToggle;
    public UserInfo mUserInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUserInfo = new UserInfo();
        dl=(DrawerLayout)findViewById(R.id.id_drawer_layout);
        mToolbar = (Toolbar)findViewById(R.id.main_toolbar);
        mToolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        mToolbar.setTitle("Home");
        setSupportActionBar(mToolbar);
        mDrawerToggle = new ActionBarDrawerToggle(this,dl,mToolbar,R.string.open,R.string.close);
        mDrawerToggle.syncState();
        dl.setDrawerListener(mDrawerToggle);
        //设置点击事件

        setDefaultFragment();
        setshadow();
        //设置默认homeFragment 

        mNavigationView = (NavigationView)findViewById(R.id.id_nv_menu);
        mNavigationView.setNavigationItemSelectedListener(mNavigationItemSelectedListener);
        //设置抽屉选择事件
        View view = mNavigationView.getHeaderView(0);
        btn_login = (ImageView)view.findViewById(R.id.id_usericon);
        TextView__username = (TextView)view.findViewById(R.id.id_username);
        TextView_maxin = (TextView)view.findViewById(R.id.id_mix);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivityForResult(intent,Login_REQUEST_CODE);

            }
        });
        //从文件读取UserInfo信息
        mUserInfo.setLogin(false);
        mUserInfo = (UserInfo)ObjectSaveUtils.getObject(MainActivity.this,"UserInfo");
        if(mUserInfo!=null && mUserInfo.isLogin()){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String t = LoadData.Login(mUserInfo.getUsername(),mUserInfo.getPassword());
                    if(t!=null){
                        mUserInfo.setCookie(t);
                        try {
                            LoadData.GetUserInfo(t,mUserInfo);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mUserInfo.setLogin(true);
                        pic_bitmap = LoadData.getHttpBitmap(mUserInfo.getPicurl());
                        mHandler.obtainMessage(85).sendToTarget();
                    }else{
                        Snackbar sBar = Snackbar.make(dl,"登陆失败,账号或密码错误!或服务器炸了(つд⊂)",Snackbar.LENGTH_SHORT);
                        View sv = sBar.getView();
                        ((TextView)sv.findViewById(R.id.snackbar_text)).setTextColor(Color.parseColor("#ffffffff"));
                        sv.setBackgroundColor(Color.parseColor("#00BCD4"));
                        sBar.show();
                    }
                }
            }).start();
        }
    }

    //负责更新UI
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==85)
                if(mUserInfo!=null && mUserInfo.isLogin()){
                    Snackbar sBar = Snackbar.make(dl,"欢迎回来，"+mUserInfo.getUsername()+"！",Snackbar.LENGTH_SHORT);
                    View sv = sBar.getView();
                    ((TextView)sv.findViewById(R.id.snackbar_text)).setTextColor(Color.parseColor("#ffffffff"));
                    sv.setBackgroundColor(Color.parseColor("#00BCD4"));
                    sBar.show();
                }
            btn_login.setImageBitmap(pic_bitmap);
            TextView__username.setText(mUserInfo.getUsername());
            TextView_maxin.setText(mUserInfo.getMaxin());
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Login_REQUEST_CODE) {
            mUserInfo = new UserInfo();
            mUserInfo =(UserInfo) data.getExtras().getSerializable("info");
            TextView__username.setText(mUserInfo.getUsername());
            TextView_maxin.setText(mUserInfo.getMaxin());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    pic_bitmap = LoadData.getHttpBitmap(mUserInfo.getPicurl());
                    mHandler.obtainMessage(85).sendToTarget();
                }
            }).start();

        }
    }

    private void setDefaultFragment() {

        FragmentManager FragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = FragmentManager.beginTransaction();

        mHomeFragment = new HomeFragment();
        transaction.replace(R.id.id_content, mHomeFragment);
        transaction.commit();
    }
    public void openStatus(){
        FragmentManager FragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = FragmentManager.beginTransaction();
        if (mStatusFragment == null) {
            mStatusFragment = new StatusFragment();
        }
        // 使用当前Fragment的布局替代id_content的控件
        setBarTitle("Status");
        transaction.replace(R.id.id_content, mStatusFragment);
        transaction.commit();
        mNavigationView.setCheckedItem(R.id.nav_status);
    }
    private void setBarTitle(String title){
        mToolbar.setTitle(title);
    }
    private void unshadow()
    {
    }
    private void setshadow()
    {
        //
    }
    private NavigationView.OnNavigationItemSelectedListener mNavigationItemSelectedListener =new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentManager FragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = FragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.nav_ranklist:
                    if (mRanklistFragment == null) {
                        mRanklistFragment = new RanklistFragment();
                    }
                    // 使用当前Fragment的布局替代id_content的控件
                    setBarTitle("Ranklist");
                    unshadow();
                    transaction.replace(R.id.id_content, mRanklistFragment);
                    break;

                case R.id.nav_status:
                    if (mStatusFragment == null) {
                        mStatusFragment = new StatusFragment();
                    }
                    // 使用当前Fragment的布局替代id_content的控件
                    setBarTitle("Status");
                    setshadow();
                    transaction.replace(R.id.id_content, mStatusFragment);
                    break;
                case R.id.nav_problems:
                    if (mProblemsFragment == null) {
                        mProblemsFragment = new ProblemsFragment();
                    }
                    // 使用当前Fragment的布局替代id_content的控件
                    setBarTitle("Problems");
                    setshadow();
                    transaction.replace(R.id.id_content, mProblemsFragment);
                    break;

                case R.id.nav_contest:
                    if (mContestFragment == null) {
                        mContestFragment = new ContestFragment();
                    }
                    // 使用当前Fragment的布局替代id_content的控件
                    setBarTitle("Contest");
                    setshadow();
                    transaction.replace(R.id.id_content, mContestFragment);
                    break;
                case R.id.nav_home:
                    if (mHomeFragment == null) {
                        mHomeFragment = new HomeFragment();
                    }
                    // 使用当前Fragment的布局替代id_content的控件
                    setBarTitle("Home");
                    setshadow();
                    transaction.replace(R.id.id_content, mHomeFragment);
                    break;
                case R.id.nav_exit:
                    iojexit();
                    return false;
                case R.id.nav_about:
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("CodeView");
                    final EditText mEditText = new EditText(MainActivity.this);
                    builder.setView(mEditText,60,0,60,0);
                    builder.setNegativeButton("cancel",null);
                    builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new CodeViewThread(mEditText.getText().toString()).start();
                            //new LoadSourceCode().start();
                        }
                    });
                    builder.show();
                    break;

            }
            transaction.commit();
            //事务提交
            item.setChecked(true);
            dl.closeDrawers();
            return false;
        }
    };
    public void iojexit(){
        finish();
    }
    class CodeViewThread extends Thread{
        private String prbid;
        CodeViewThread(String prbid){
            this.prbid = prbid;
        }
        @Override
        public void run() {
            String acid = null;
            try {
                acid = LoadData.getAcId(this.prbid.toString(),mUserInfo.getUsername());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(acid == null){
                Snackbar sBar = Snackbar.make(dl,"找不到该题目的AC代码",Snackbar.LENGTH_SHORT);
                View sv = sBar.getView();
                ((TextView)sv.findViewById(R.id.snackbar_text)).setTextColor(Color.parseColor("#ffffffff"));
                sv.setBackgroundColor(Color.parseColor("#00BCD4"));
                sBar.show();
            }else {
                Intent intent = new Intent(MainActivity.this, CodeViewActivity.class);
                intent.putExtra("acid", acid);
                intent.putExtra("cookies", mUserInfo.getCookie());
                intent.putExtra("username", mUserInfo.getUsername());
                startActivity(intent);
            }
        }
    }
    class LoadSourceCode extends Thread{
        @Override
        public void run() {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                System.out.println("加载mysql驱动成功");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            String url="jdbc:mysql://ioj.mysqldb.chinacloudapi.cn:3306/ioj";    //JDBC的URL
            Connection conn = null;
            Statement stmt = null;
            try {
                conn = DriverManager.getConnection(url,"ioj%wax","Asd5603312"); //创建一个Statement对象
                stmt = conn.createStatement(); //创建Statement对象
                System.out.print("成功连接到数据库！");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            String acid=null,id=null,code=null;
            title mtitle = null;
            String[] prbid={"0015", "0020", "0021", "0022", "0023", "0025", "0026", "0027", "0028", "0029", "0030", "0031", "0032", "0033", "0034", "0035", "0036", "0037", "0038", "0039", "0040", "0041", "0042", "0044", "0045", "0046", "0047", "0048", "0056", "0066", "0067", "0068", "0069", "0070", "0072", "0074", "0076", "0077", "0078", "0080", "0082", "0084", "0086", "0109", "0116", "0117", "0119", "0129", "0133", "0139", "0140", "0141", "0142", "0143", "0144", "0147", "0148", "0149", "0150", "0152", "0160", "0161", "0162", "0167", "0170", "0171", "0172", "0179", "0189", "0190", "0192", "0203", "0220", "0221", "0226", "0227", "0232", "0242", "0254", "0256", "0259", "0273", "0276", "0277", "0278", "0279", "0280", "0281", "0282", "0284", "0285", "0287", "0288", "0289", "0290", "0291", "0292", "0293", "0294", "0295", "0297", "0299", "0308", "0310", "0311", "0312", "0313", "0314", "0315", "0316", "0318", "0319", "0320", "0321", "0331", "0333", "0334", "0335", "0336", "0341", "0342", "0361", "0405", "0413", "0416", "0423", "0424", "0426", "0427", "0430", "0434", "0435", "0437", "0440", "0442", "0446", "0448", "0470", "0471", "0480", "0484", "0485", "0489", "0494", "0507", "0509", "0524", "0533", "0540", "0541", "0554", "0557", "0558", "0559", "0563", "0566", "0571", "0580", "0597", "0599", "0600", "0601", "0605", "0606", "0607", "0612", "0613", "0614", "0615", "0616", "0617", "0618", "0619", "0620", "0622", "0623", "0625", "0642", "0646", "0667", "0695", "0700", "0706", "0736", "0740", "0760", "0777", "0832", "0833", "0834", "0860", "0921", "0922", "0941", "0942", "0953", "0954", "0956", "0957", "0959", "0961", "0962", "0963", "0966", "0967", "1014", "1016", "1027", "1028", "1035", "1036", "1038", "1039", "1044", "1046", "1067", "1090", "1099", "1101", "1103", "1107", "1109", "1110", "1114", "1121", "1139", "1156", "1157", "1158", "1159", "1160", "1161", "1162", "1163", "1164", "1165", "1166", "1167", "1168", "1169", "1170", "1171", "1172", "1173", "1174", "1175", "1176", "1177", "1178", "1179", "1180", "1181", "1182", "1183", "1184", "1185", "1186", "1187", "1188", "1189", "1190", "1191", "1192", "1193", "1194", "1195", "1210", "1215" };
            file f = new file();
            for(int i = 0; i<prbid.length;i++){
                id = prbid[i];
                try {
                    acid = LoadData.getAcId(id,mUserInfo.getUsername());
                    mtitle = new title("");
                    code = LoadData.getMyCode(acid,mUserInfo.getCookie(),mUserInfo.getUsername(),id,mtitle);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                code = code.replace("\r","\r\n");
                code = code.replace("&lt;","<");
                code = code.replace("&gt;",">");
                code = code.replace("&nbsp;"," ");
                code = code.replace("&amp;"," ");
                //code = Html.fromHtml(code).toString();
                //f.writeTxtToFile(code,"/sdcard/Test/",id+"-"+mtitle.getTitle()+".txt");
                String insertsql = "insert into sourcecode(prbid,prbtitle,code,contributor,notes)"
                        + " values('"+id+"','"+mtitle.getTitle()+"','"+code+"','admin','12.26')";
                try {
                    //stmt.execute(insertsql);
                    stmt.executeUpdate(insertsql);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                System.out.println(id+"-"+mtitle.getTitle()+".txt");
                try {
                    Thread.currentThread().sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            try {
                stmt.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis()-exitTime) > 1500){
                Snackbar sBar = Snackbar.make(dl,"再按一次退出程序",Snackbar.LENGTH_SHORT);
                View sv = sBar.getView();
                ((TextView)sv.findViewById(R.id.snackbar_text)).setTextColor(Color.parseColor("#ffffffff"));
                sv.setBackgroundColor(Color.parseColor("#00BCD4"));
                sBar.show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
