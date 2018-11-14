package com.suntiago.baseui.lockPattern;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import com.suntiago.baseui.R;
import com.suntiago.baseui.activity.SlothActivity;
import com.suntiago.baseui.lockPattern.util.ACache;
import com.suntiago.baseui.lockPattern.util.LockPatternUtil;
import com.suntiago.baseui.lockPattern.widget.LockPatternView;

import static com.suntiago.baseui.lockPattern.PatternManager.GESTURE_PASSWORD;


/**
 * Created by Sym on 2015/12/24.
 */
public class GestureLoginActivity extends SlothActivity {

    private static final String TAG = "LoginGestureActivity";
    LockPatternView lockPatternView;
    TextView messageTv;
    Button forgetGestureBtn;

    private ACache aCache;
    private static final long DELAYTIME = 600l;
    private byte[] gesturePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesture_login);
        lockPatternView = (LockPatternView) findViewById(R.id.lockPatternView);
        messageTv = (TextView) findViewById(R.id.messageTv);
        forgetGestureBtn = (Button) findViewById(R.id.forgetGestureBtn);
        this.init();
        forgetGestureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /**
                 * 忘记手势密码（去账号登录界面）
                 */
                PatternManager.gett().patternForget();
                finish();
            }
        });
    }

    private void init() {
        aCache = ACache.get(GestureLoginActivity.this);
        //得到当前用户的手势密码
        gesturePassword = aCache.getAsBinary(GESTURE_PASSWORD + PatternManager.gett().getAccountID());
        lockPatternView.setOnPatternListener(patternListener);
        updateStatus(Status.DEFAULT);
    }

    private LockPatternView.OnPatternListener patternListener = new LockPatternView.OnPatternListener() {

        @Override
        public void onPatternStart() {
            lockPatternView.removePostClearPatternRunnable();
        }

        @Override
        public void onPatternComplete(List<LockPatternView.Cell> pattern) {
            if (pattern != null) {
                if (LockPatternUtil.checkPattern(pattern, gesturePassword)) {
                    updateStatus(Status.CORRECT);
                } else {
                    updateStatus(Status.ERROR);
                }
            }
        }
    };

    /**
     * 更新状态
     *
     * @param status
     */
    private void updateStatus(Status status) {
        messageTv.setText(status.strId);
        messageTv.setTextColor(getResources().getColor(status.colorId));
        switch (status) {
            case DEFAULT:
                lockPatternView.setPattern(LockPatternView.DisplayMode.DEFAULT);
                break;
            case ERROR:
                lockPatternView.setPattern(LockPatternView.DisplayMode.ERROR);
                lockPatternView.postClearPatternRunnable(DELAYTIME);
                break;
            case CORRECT:
                lockPatternView.setPattern(LockPatternView.DisplayMode.DEFAULT);
                loginGestureSuccess();
                break;
        }
    }

    /**
     * 手势登录成功（去首页）
     */
    private void loginGestureSuccess() {
        PatternManager.gett().patternChecked();
        finish();
    }


    private enum Status {
        //默认的状态
        DEFAULT(R.string.gesture_default, R.color.grey_a5a5a5),
        //密码输入错误
        ERROR(R.string.gesture_error, R.color.red_f4333c),
        //密码输入正确
        CORRECT(R.string.gesture_correct, R.color.grey_a5a5a5);

        private Status(int strId, int colorId) {
            this.strId = strId;
            this.colorId = colorId;
        }

        private int strId;
        private int colorId;
    }

    @Override
    public void onBackPressed() {

    }
}
