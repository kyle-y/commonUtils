package com.example.administrator.common.commonUtils;

//public class ImageWithIconAdapter extends BaseAdapter {
//	public static final int TYPE_URL = 0;
//	public static final int TYPE_LOCAL = 1;
//
//	protected Context context;
//	protected List<TypeImage> list;
//	protected LayoutInflater inflater;
//	private int hight;
//	private ChooseButtonOnClickListener clickListener;
//	private int mCount = 0;
//
//	public ImageWithIconAdapter(Context context, ChooseButtonOnClickListener clickListener) {
//		this.context = context;
//		this.clickListener = clickListener;
//		inflater = LayoutInflater.from(context);
//		list = new ArrayList<>();
//		hight = (CommonUtil.getWindowWidth(context) - CommonUtil.dipsToPixels(context, 42)) / 4 ;
//	}
//
//	public void initData() {
//		list.clear();
//		TypeImage typeImage = new TypeImage();
//		typeImage.setType(TYPE_LOCAL);
//		list.add(typeImage);
//	}
//
//	public List<String> getDataUrl() {
//		List<String> urls = new ArrayList<>();
//		for (TypeImage typeImage : list){
//			if (typeImage.getType() == TYPE_URL){
//				urls.add(typeImage.getUrl());
//			}
//		}
//		return urls;
//	}
//	@Override
//	public int getCount() {
//		return list.size();
//	}
//
//	public void addData(TypeImage typeImage) {
//		if (getCount() >= 1){
//			list.remove(list.size() - 1);
//			list.add(typeImage);
//			TypeImage typeImageLocal = new TypeImage();
//			typeImageLocal.setType(TYPE_LOCAL);
//			list.add(typeImageLocal);
//		}
//		if (getUrlTypeCount() == 4){
//			list.remove(4);
//		}
//	}
//
//	public void setData(List<TypeImage> typeImages) {
//		list.clear();
//		list.addAll(typeImages);
//		if (typeImages.size() < 4) {
//			TypeImage typeImageLocal = new TypeImage();
//			typeImageLocal.setType(TYPE_LOCAL);
//			list.add(typeImageLocal);
//		}
//	}
//
//	@Override
//	public Object getItem(int position) {
//		return list.get(position);
//	}
//
//	@Override
//	public long getItemId(int position) {
//		return position;
//	}
//
//	public void removeItem(int position){
//		list.remove(position);
//		if (getUrlTypeCount() == 3){
//			TypeImage typeImageLocal = new TypeImage();
//			typeImageLocal.setType(TYPE_LOCAL);
//			list.add(typeImageLocal);
//		}
//		notifyDataSetChanged();
//	}
//
//	public void removeAll(){
//		list.clear();
//		TypeImage typeImageLocal = new TypeImage();
//		typeImageLocal.setType(TYPE_LOCAL);
//		list.add(typeImageLocal);
//		notifyDataSetChanged();
//	}
//
//	public int getUrlTypeCount(){
//		int temp = 0;
//		for (TypeImage typeImage : list){
//			if (typeImage.getType() == TYPE_URL){
//				temp ++;
//			}
//		}
//		return temp;
//	}
//	@Override
//	public View getView(final int position, View view, ViewGroup parent) {
//		ViewHolder viewHolder;
//
//
//		if (view == null) {
//			viewHolder = new ViewHolder();
//			view = inflater.inflate(R.layout.item_image, null);
//			viewHolder.iv_imag = (FilletImageView) view.findViewById(R.id.iv_ii_img);
//			viewHolder.image_delete = (ImageView) view.findViewById(R.id.iv_delete);
//			view.setTag(viewHolder);
//		} else {
//			viewHolder = (ViewHolder) view.getTag();
//		}
//		viewHolder.iv_imag.setRectAdius(4);
//		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) viewHolder.iv_imag.getLayoutParams();
//		params.width = hight;
//		params.height = hight;
//		viewHolder.iv_imag.setLayoutParams(params);
//		if (((NoScrollGridView)parent).isOnMeasure) return view;
//		if (list.get(position).getType() == TYPE_LOCAL){
//			viewHolder.image_delete.setVisibility(View.GONE);
//			viewHolder.iv_imag.setImageResource(R.drawable.seletor_choose_picture);
//			viewHolder.iv_imag.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					clickListener.click();
//				}
//			});
//		}else if (list.get(position).getType() == TYPE_URL){
//			viewHolder.image_delete.setVisibility(View.VISIBLE);
//			String imgUrl = list.get(position).getUrl();
//			if (!imgUrl.equals("")) {
//				if (!imgUrl.contains("http://")) {
//					imgUrl = "file://" + imgUrl;
//				}
//				LogUtil.e("url===="+imgUrl);
//				ImageUtils.displayImage(imgUrl, viewHolder.iv_imag, Global.options);
//			}
//			viewHolder.iv_imag.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View view) {
//					ImageViewPagerActivity.imageSize = new ImageSize(view.getMeasuredWidth(), view.getMeasuredHeight());
//					ImageViewPagerActivity.startImagePagerActivity(context, getDataUrl(), position+1, true);
//				}
//			});
//			viewHolder.image_delete.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					removeItem(position);
//				}
//			});
//		}
//		return view;
//	}
//
//	class ViewHolder{
//		FilletImageView iv_imag;
//		ImageView image_delete;
//	}
//
//	public interface ChooseButtonOnClickListener{
//		void click();
//	}

//}
