package android.com.fenguoz.customview;

import android.app.Activity;
import android.com.fenguoz.R;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;



public class SelectPicPopupWindow extends PopupWindow {
	private TextView cancel;
	private View mMenuView;

	public SelectPicPopupWindow(Activity context, OnClickListener itemsOnClick) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView = inflater.inflate(R.layout.shared_popupwindow, null);
		this.mMenuView.findViewById(R.id.dialog_weixin).setOnClickListener(
				itemsOnClick);
		this.mMenuView.findViewById(R.id.dialog_weixinmoments)
				.setOnClickListener(itemsOnClick);
		this.mMenuView.findViewById(R.id.dialog_sinaweibo).setOnClickListener(
				itemsOnClick);
		/*this.mMenuView.findViewById(R.id.dialog_msg).setOnClickListener(
				itemsOnClick);*/
		this.mMenuView.findViewById(R.id.dialog_qq).setOnClickListener(
				itemsOnClick);
		this.mMenuView.findViewById(R.id.dialog_qone).setOnClickListener(
				itemsOnClick);
		this.mMenuView.findViewById(R.id.dialog_cancel_lay).setOnClickListener(
				itemsOnClick);
		//新增的联系人
		this.mMenuView.findViewById(R.id.lianxin).setOnClickListener(
				itemsOnClick);
		this.mMenuView.findViewById(R.id.pop_layout).setOnClickListener(
				itemsOnClick);
		
		
		this.setWidth(LayoutParams.MATCH_PARENT);
		this.setHeight(LayoutParams.WRAP_CONTENT);
		this.setBackgroundDrawable(new BitmapDrawable());
		this.setOutsideTouchable(true);
		this.setFocusable(true);

		// 邀请窝粉才有通讯录分享
		/*if (context instanceof InviteFriendActivity) {
			this.mMenuView.findViewById(R.id.dialog_msg).setVisibility(
					View.VISIBLE);
		}
*/
		this.setContentView(mMenuView);

		this.setAnimationStyle(R.style.AnimBottom);
		mMenuView.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {

				int height = mMenuView.findViewById(R.id.pop_layout).getTop();
				int y = (int) event.getY();
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (y < height) {
						dismiss();
					}
				}
				return true;
			}
		});

	}
}
