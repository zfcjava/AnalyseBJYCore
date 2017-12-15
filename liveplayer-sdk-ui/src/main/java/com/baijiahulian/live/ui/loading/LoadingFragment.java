package com.baijiahulian.live.ui.loading;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ProgressBar;

import com.baijiahulian.live.ui.R;
import com.baijiahulian.live.ui.base.BaseFragment;
import com.baijiahulian.livecore.LiveSDK;
import com.baijiahulian.livecore.context.LPError;
import com.baijiahulian.livecore.context.LiveRoom;

import static com.baijiahulian.live.ui.utils.Precondition.checkNotNull;

/**
 * Created by Shubo on 2017/2/14.
 */

public class LoadingFragment extends BaseFragment implements LoadingContract.View {

    private LoadingContract.Presenter presenter;
    private ProgressBar progressBar;

    @Override
    public void setPresenter(LoadingContract.Presenter presenter) {
        super.setBasePresenter(presenter);
        this.presenter = presenter;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_loading;
    }

    @Override
    //Attention 只有将Fragment添加到Activity中才会走该方法
    public void init(Bundle savedInstanceState) {
        checkNotNull(presenter);
        progressBar = (ProgressBar) $.id(R.id.fragment_loading_pb).view();
        Bundle args = getArguments();
        if (args != null) {
            setTechSupportVisibility(args.getBoolean("show_tech_support", true));
        } else {
            setTechSupportVisibility(true);
        }
        LiveRoom room;
        if (presenter.isJoinCode()) {
            room = LiveSDK.enterRoom(getActivity(), presenter.getCode(), presenter.getName(), presenter.getLaunchListener());
        } else {
            //Attention 从这里可以看出，MVP的使用环境，及时存在着“铁板一块”的sdk，我们也可以将它抽象成一个点
            room = LiveSDK.enterRoom(getActivity(), presenter.getRoomId(), presenter.getUser().getNumber(),
                    presenter.getUser().getName(), presenter.getUser().getType(), presenter.getUser().getAvatar(),
                    presenter.getSign(), presenter.getLaunchListener());
        }
        presenter.setLiveRoom(room);
    }

    private ObjectAnimator animator;

    @Override
    public void showLoadingSteps(int currentStep, int totalSteps) {
        int start = progressBar.getProgress();
        int end = currentStep * 100 / totalSteps;
        if (animator != null && animator.isRunning()) {
            animator.cancel();
        }
        animator = ObjectAnimator.ofInt(progressBar, "progress", start, end);
        animator.setDuration(400);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.start();
    }

    @Override
    public void showLaunchError(LPError lpError) {
//        showToast(lpError.getMessage());
    }

    @Override
    public void setTechSupportVisibility(boolean shouldShow) {
        if (shouldShow) {
            $.id(R.id.tv_fragment_loading_tech_support).visible();
        } else {
            $.id(R.id.tv_fragment_loading_tech_support).invisible();
        }
    }
}
