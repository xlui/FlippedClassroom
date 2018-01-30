package io.flippedclassroom.android.presenterImpl;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.flippedclassroom.android.R;
import io.flippedclassroom.android.activity.CourseActivity;
import io.flippedclassroom.android.activity.CourseInfoActivity;
import io.flippedclassroom.android.activity.SettingActivity;
import io.flippedclassroom.android.adapter.CourseAdapter;
import io.flippedclassroom.android.base.BasePresenter;
import io.flippedclassroom.android.bean.Course;
import io.flippedclassroom.android.bean.User;
import io.flippedclassroom.android.json.CourseJson;
import io.flippedclassroom.android.model.CourseModel;
import io.flippedclassroom.android.presenter.CoursePresenter;
import io.flippedclassroom.android.util.LogUtils;
import io.flippedclassroom.android.util.RetrofitManager;
import io.flippedclassroom.android.util.ToastUtils;
import io.flippedclassroom.android.util.RetrofitUtils;
import io.flippedclassroom.android.view.CourseView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;

public class CoursePresenterImpl extends BasePresenter implements CoursePresenter {
    private Handler mHandler = new Handler(Looper.myLooper());
    private boolean canQuit = false;
    private CourseView mView;
    private CourseModel mModel;

    public CoursePresenterImpl(CourseActivity activity, Context mContext) {
        super(mContext);
        mView = activity;
        mModel = new CourseModel(mContext);
    }

    @Override
    public void onClick(int viewId) {
        switch (viewId) {
            case R.id.menu_login_out:
                loginOut();
            case R.id.menu_setting:
                mContext.startActivity(new Intent(mContext, SettingActivity.class));
        }
    }

    private void loginOut() {
        mModel.deleteId();
        mModel.deleteRole();
        mModel.deleteToken();
        mView.returnLoginActivity();
    }

    @Override
    public void onRefresh() {
        getCourseList();
        getAvatar();
        getUserInfo();
    }

    private void getAvatar() {
//        OkHttpClient client = new OkHttpClient();
//        Request request = new Request.Builder().url("https://fc.xd.style/avatar").addHeader("Authorization", mModel.getToken()).build();
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//
//            }
//        });
    }

    //从服务器拉取用户信息
    private void getUserInfo() {
        //发起网络请求,拉取用户信息
        Retrofit retrofit = RetrofitManager.getRetrofit();
        RetrofitUtils.AccountService accountService = retrofit.create(RetrofitUtils.AccountService.class);
        accountService.getProfile(mModel.getToken())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<User>() {
                    @Override
                    public void accept(User user) throws Exception {
                        //处理返回结果
                        parseUserInfo(user);
                    }
                });
    }

    //对用户信息拉取结果的处理
    private void parseUserInfo(User user) {
        String nickName = user.getNickName();
        if (TextUtils.isEmpty(user.getNickName())) {
            nickName = mContext.getString(R.string.default_user_name);
        }

        String signature = user.getSignature();
        if (TextUtils.isEmpty(user.getSignature())) {
            signature = mContext.getString(R.string.default_user_description);
        }

        //更新顶部的信息
        mView.updateHeaderLayout(nickName, signature);
    }


    //从服务器拉取课程
    private void getCourseList() {
        //发起网络请求,拉取课程列表
        Retrofit retrofit = RetrofitManager.getRetrofit();
        RetrofitUtils.CourseService courseService = retrofit.create(RetrofitUtils.CourseService.class);
        courseService.getCourseList(mModel.getToken())
                //map变换
                .map(new Function<CourseJson, List<Course>>() {
                    @Override
                    public List<Course> apply(CourseJson courseJson) throws Exception {
                        return courseJson.getCoursesList();
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Course>>() {
                    @Override
                    public void accept(List<Course> courseList) throws Exception {
                        //处理返回的课程列表
                        parseCourseList(courseList);
                    }
                });
    }

    private void parseCourseList(List<Course> courseList) {
        ToastUtils.createToast("加载成功");
        //存入model
        mModel.saveCourseList(courseList);

        //updateList
        CourseAdapter adapter = new CourseAdapter(courseList, CoursePresenterImpl.this);
        mView.updateCourseList(adapter);
        //结束刷新
        mView.onRefreshFinish();
    }

    @Override
    public void onQuery(String queryText) {
        List<Course> list = mModel.getCourseList();
        List<Course> newList = new ArrayList<>();
        for (Course course : list) {
            //匹配课程名字
            if (queryText.equals(course.getName())) {
                newList.add(course);
            }
        }
        //刷新课程列表
        CourseAdapter adapter = new CourseAdapter(newList, this);
        mView.updateCourseList(adapter);
    }

    @Override
    public void onSearchClose() {
        //如果关闭了搜索，恢复之前的样子
        CourseAdapter adapter = new CourseAdapter(mModel.getCourseList(), this);
        mView.updateCourseList(adapter);
    }

    @Override
    public void onBack() {
        //两次点击退出应用的逻辑
        if (canQuit) {
            //如果快速点击两次就会到这里
            System.exit(0);
        } else {
            //单击一次
            canQuit = true;
            ToastUtils.createToast("再次点击退出应用");
            //两秒以后把标志重新置为false
            //如果两秒以内再次点击了返回，if判断成功，整个应用结束
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    canQuit = false;
                }
            }, 2000);
        }
    }

    @Override
    public void onClick(int viewId, int position) {
        switch (viewId) {
            //如果点击了查看课程的item
            case R.id.pop_menu_look:
                startCourseInfoActivity(position);
                break;
            //如果点击了删除课程的item
            case R.id.pop_menu_delete:
                deleteCourse(position);
        }
    }

    //删除课程的方法
    private void deleteCourse(final int position) {
        //获得选择课程额Id
        int courseId = mModel.getCourseList().get(position).getId();
        //Retrofit发起请求
        Retrofit retrofit = RetrofitManager.getRetrofit();
        RetrofitUtils.CourseService courseService = retrofit.create(RetrofitUtils.CourseService.class);
        courseService.deleteCourse(mModel.getToken(), courseId).enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {

                mModel.getCourseList().remove(position);
                CourseAdapter adapter = new CourseAdapter(mModel.getCourseList(), CoursePresenterImpl.this);
                mView.updateCourseList(adapter);
            }

            @Override
            public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    //前往课程信息界面
    private void startCourseInfoActivity(int position) {
        Intent intent = new Intent(mContext, CourseInfoActivity.class);
        //把课程序列化进去
        //在创建Activity的时候重新读取数据
        intent.putExtra("Course", mModel.getCourseList().get(position));
        mContext.startActivity(intent);
    }


    @Override
    public Context getContext() {
        return mContext;
    }
}