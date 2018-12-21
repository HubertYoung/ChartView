package com.yt.crossline;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import com.yt.linechart.ChartNode;
import com.yt.utils.LogUtils;
import com.yt.utils.Utils;

import java.util.ArrayList;


/**
 * <br>
 * com.yt
 *
 * @author lei
 * @version 1.0
 * @date 2018/8/21 上午10:00
 */
public class CrossLineChartView extends RelativeLayout {
	/**
	 * View 的宽和高
	 */
	private int mWidth, mHeight;
	private Context mContext;
	public static final int marginBottom = 35;

	public int mPartLength;
	public static final String TAG = CrossLineChartView.class.getSimpleName();

	private CrossYLine yLine;
	private CrossXLine xLine;
	private CrossLineCoordinate mInComCoordinate;
	private CrossLineCoordinate mOutComCoordinate;
	private ArrayList< ChartNode > xPoints;

	private ArrayList< ChartNode > mPayNodes = new ArrayList<>();
	private ArrayList< ChartNode > mRecNodes = new ArrayList<>();

	private ChartNode mPayCurrentNode, mRecCurrentNode;

	private CrossLineCoordinate.ColorBean mPayColorBean;

	private CrossLineCoordinate.ColorBean mRecColorBean;

	private InvalidataChart mInvalidataChart;
	private String mPayDotText;
	private String mRcvDotText;


	public interface InvalidataChart {
		void invalidate();
	}

	public void setInvalidataChart( InvalidataChart invalidataChart ) {
		mInvalidataChart = invalidataChart;
	}

	public CrossLineChartView( Context context ) {
		super( context );
		this.mContext = context;
	}

	public void setPartLength( int partLength ) {
		mPartLength = partLength;
	}

	public CrossLineChartView( Context context, @Nullable AttributeSet attrs ) {
		super( context, attrs );
		this.mContext = context;
		ViewTreeObserver obserrve = getViewTreeObserver();
		obserrve.addOnGlobalLayoutListener( new ViewTreeObserver.OnGlobalLayoutListener() {

			@Override
			public void onGlobalLayout() {
				LogUtils.iTag( TAG, "onGlobalLayout view绘制前" );
				if ( 0 != getMeasuredWidth() ) {
					LogUtils.iTag( TAG, "onGlobalLayout view开始绘制" );

					if ( mInvalidataChart != null ) {
						mInvalidataChart.invalidate();
					}
					getViewTreeObserver().removeGlobalOnLayoutListener( this );

				}

			}
		} );
	}


	public CrossLineChartView( Context context, @Nullable AttributeSet attrs, int defStyleAttr ) {
		super( context, attrs, defStyleAttr );
		this.mContext = context;

	}

	public int getLineChartHeight() {
		return this.mHeight;
	}

	public void setxPoints( ArrayList< ChartNode > xPoints ) {
		this.xPoints = xPoints;
	}

	public void setRecNodes( ArrayList< ChartNode > recNodes ) {
		mRecNodes = recNodes;
	}

	public void setPayNodes( ArrayList< ChartNode > payNodes ) {
		mPayNodes = payNodes;
	}


	public void setPayColorBean( CrossLineCoordinate.ColorBean payColorBean ) {
		mPayColorBean = payColorBean;
	}

	public void setRecColorBean( CrossLineCoordinate.ColorBean recColorBean ) {
		mRecColorBean = recColorBean;
	}

	public void setPayDotTextBean( String payDotText ) {
		mPayDotText = payDotText;
	}

	public void setRecDotTextBean( String rcvDotText ) {
		mRcvDotText = rcvDotText;
	}

	@Override
	public boolean onTouchEvent( MotionEvent event ) {
		switch ( event.getAction() ) {
			case MotionEvent.ACTION_DOWN:
				float x = event.getX();
				float y = event.getY();
				if ( y <= Utils.dp2px( mContext, 45 ) ) {
					return true;
				}

				if ( mRecNodes.size() > 1 ) {
					float x1 = mRecNodes.get( 0 ).getX();
					float space = mRecNodes.get( 1 ).getX() - x1;
					int length = mRecNodes.size();
//					for (int i = 0; i < length; i++) {
//						float xPoint = mRecNodes.get( i ).getX();
//						if ( mRecNodes.get( 0 ).getX() <= x && x <= mRecNodes.get( length - 1 ).getX() ) {
//							//x在小于等于x轴间距的一半的时候，点击触发的是上一个点数据显示
//							if ( x <= ( xPoint + space / 2 ) ) {
//								updateChartNodesFlag( mRecNodes, x );
//								//底部坐标更新flag标示
//								updateChartNodesFlag( xPoints, x );
//								//从新绘制
//								invalidate();
//							}else if( x > ( xPoint + space / 2 )) {
//                             //x在大于x轴间距的一半的时候，点击触发的下一个点数据显示
//
//							}
//						}

					float x2 = mRecNodes.get( length - 1 ).getX();
					LogUtils.eTag( TAG,"mRecNodes.get( 0 ).getX()"+ x1);
					LogUtils.eTag( TAG,"mRecNodes.get( length - 1 ).getX()"+ x2);
					if (mRecNodes.get( length - 1 ).getX() <= x && x <= x1 ) {
						updateChartNodesFlag( mRecNodes, x );
						//底部坐标更新flag标示
						updateChartNodesFlag( xPoints, x );
						//从新绘制
						invalidate();
					}
				}


				break;
			case MotionEvent.ACTION_MOVE:
				break;
			case MotionEvent.ACTION_UP:
				break;
			default:
				break;
		}
		return true;
	}


	private void updateChartNodesFlag( ArrayList< ChartNode > nodes, float x ) {
		for (ChartNode chartNode : nodes) {
			float max = chartNode.getX() + mPartLength / 2;
			float min = chartNode.getX() - mPartLength / 2;
			if ( x < max && x > min ) {
				chartNode.setFlag( true );
			} else {
				chartNode.setFlag( false );
			}
		}

	}

	@Override
	protected void onDraw( Canvas canvas ) {
		super.onDraw( canvas );
		LogUtils.i( TAG, "onDraw" );
		//画x轴
		xLine = new CrossXLine( canvas, mContext, xPoints );
		xLine.setChartNodes( mRecNodes );
		xLine.drawLine( mWidth, mHeight );
		xLine.drawXYearText();
		//画收入的点
		mInComCoordinate = new CrossLineCoordinate( mWidth, mHeight, canvas, mContext );
		mInComCoordinate.setxPoints( xPoints );
		//view上面显示个点
		mInComCoordinate.setNodes( mRecNodes );
		//设置颜色
		mInComCoordinate.setColorBean( mRecColorBean );

		//画支出的点
		mOutComCoordinate = new CrossLineCoordinate( mWidth, mHeight, canvas, mContext );
		mOutComCoordinate.setxPoints( xPoints );
		//view上面显示个点
		mOutComCoordinate.setNodes( mPayNodes );
		//设置颜色
		mOutComCoordinate.setColorBean( mPayColorBean );

		//画y轴
		yLine = new CrossYLine( canvas, mContext, xPoints );
		if ( xPoints != null && xPoints.size() > 0 ) {
			yLine.drawLine( mWidth, mHeight );
		}
		if ( xPoints != null && mPayNodes != null && mPayColorBean != null ) {
			//支出
			mInComCoordinate.drawNode();
		}
		if ( xPoints != null && mRecNodes != null && mRecColorBean != null ) {
			//收入
			mOutComCoordinate.drawNode();
		}

		mPayCurrentNode = mOutComCoordinate.mCurrentChartNode;
		mRecCurrentNode = mInComCoordinate.mCurrentChartNode;

		//绘制填充色
		if ( mPayNodes != null && mRecNodes != null ) {
			fullColor( canvas );
		}


		//绘制收支明细顶端的收入支出点和金额
		canvasTopDot( canvas );
	}

	/**
	 * 绘制收支明细顶端的收入支出点和金额
	 */
	private void canvasTopDot( Canvas canvas ) {
		//画点支出点
		Paint paint = new Paint();
		paint.setAntiAlias( true );
		//设置线条宽度
		paint.setStyle( Paint.Style.FILL );
		paint.setStrokeWidth( Utils.dp2px( mContext, 1 ) );
		paint.setTextAlign( Paint.Align.LEFT );
		//画笔大小
		paint.setTextSize( Utils.dp2px( mContext, 12 ) );

		int payX = Utils.dp2px( mContext, 190 ), payY = Utils.dp2px( mContext, 30 );
		int recX = Utils.dp2px( mContext, 40 ), recY = payY;

		int offset1 = Utils.dp2px( mContext, 13 );
		int offset2 = Utils.dp2px( mContext, 30 );
		int offset3 = Utils.dp2px( mContext, 5 );

		//画点
		int pointRadius = Utils.dp2px( mContext, 4 );
		paint.setColor( Color.parseColor( "#FC9596" ) );
		canvas.drawCircle( payX, payY - offset3, pointRadius, paint );
		paint.setColor( Color.parseColor( "#5498EE" ) );
		canvas.drawCircle( recX, recY - offset3, pointRadius, paint );

		if ( !TextUtils.isEmpty( mPayDotText ) && !TextUtils.isEmpty( mRcvDotText ) ) {
			//画文字
			paint.setColor( Color.parseColor( "#333333" ) );
			canvas.drawText( mPayDotText, payX + offset1, payY, paint );
			canvas.drawText( mRcvDotText, recX + offset1, recY, paint );

		}
		if ( mPayCurrentNode != null && mRecCurrentNode != null ) {
			//画金额
			paint.setColor( Color.parseColor( "#FC9596" ) );
			canvas.drawText( mPayCurrentNode.getText(), payX + offset1 + offset2, payY, paint );
			paint.setColor( Color.parseColor( "#5498EE" ) );
			canvas.drawText( mRecCurrentNode.getText(), recX + offset1 + offset2, recY, paint );
		}

	}


	private ChartNode getConnectionChartNode( ChartNode payNode1, ChartNode payNode2, ChartNode recNode1, ChartNode recNode2 ) {
		//设已知的一条线段的起点和终点为(x1,y1),(x2,y2)，另一条为 (x3,y3),(x4,y4)
		float x0 = payNode1.getX();
		float y0 = payNode1.getY();

		float x1 = payNode2.getX();
		float y1 = payNode2.getY();

		float x2 = recNode1.getX();
		float y2 = recNode1.getY();

		float x3 = recNode2.getX();
		float y3 = recNode2.getY();

		float y = ( ( y0 - y1 ) * ( y3 - y2 ) * x0 + ( y3 - y2 ) * ( x1 - x0 ) * y0 + ( y1 - y0 ) * ( y3 - y2 ) * x2 + ( x2 - x3 ) * ( y1 - y0 ) * y2 ) / ( ( x1 - x0 ) * ( y3 - y2 ) + ( y0 - y1 ) * ( x3 - x2 ) );
		float x = x2 + ( x3 - x2 ) * ( y - y2 ) / ( y3 - y2 );

		LogUtils.i( TAG, "交点坐标===：x:" + x + " y:" + y );

		return new ChartNode( x, y, "" );
	}


	private void fullColor( Canvas canvas ) {
		int size = mPayNodes.size();
		for (int i = 0; i < mPayNodes.size(); i++) {
			int s = i + 1;

			if ( s <= size - 1 ) {
				fullColor( canvas, mPayNodes.get( i ), mPayNodes.get( s ), mRecNodes.get( i ), mRecNodes.get( s ) );
			}

		}

	}

	private void fullColor( Canvas canvas, ChartNode payNode1, ChartNode payNode2, ChartNode recNode1, ChartNode recNode2 ) {
		//两条折线的交点
		ChartNode node = getConnectionChartNode( payNode1, payNode2, recNode1, recNode2 );

		if ( Float.isInfinite( node.getX() ) || Float.isInfinite( node.getY() ) || Float.isNaN( node.getX() ) || Float.isNaN( node.getY() ) ) {
			//每两个点相等时，即两条线重合，不涂色
			if ( payNode1.getY() == recNode1.getY() && payNode2.getY() == recNode2.getY() ) {
				return;
			}
			Path path = new Path();
			path.moveTo( payNode1.getX(), payNode1.getY() );
			path.lineTo( payNode2.getX(), payNode2.getY() );
			path.lineTo( recNode2.getX(), recNode1.getY() );
			path.lineTo( recNode1.getX(), recNode1.getY() );
			Paint paint = null;
			if ( payNode1.getY() > recNode1.getY() ) {
				paint = getPaint( mRecColorBean.textColor, mRecColorBean.textColor );

			} else {
				paint = getPaint( mPayColorBean.textColor, mPayColorBean.textColor );

			}
			//根据Path进行绘制，绘制五角星
			canvas.drawPath( path, paint );
			path.close();
			return;
		}

		if ( node.getX() > payNode1.getX() && node.getX() < payNode2.getX() ) {

			//定义一个Path对象，封闭一个多边形
			Path path1 = new Path();
			path1.moveTo( payNode1.getX(), payNode1.getY() );
			path1.lineTo( node.getX(), node.getY() );
			path1.lineTo( recNode1.getX(), recNode1.getY() );

			Paint paint1 = null;
			if ( payNode1.getY() > recNode1.getY() ) {
				paint1 = getPaint( mRecColorBean.textColor, mRecColorBean.textColor );
			} else {
				paint1 = getPaint( mPayColorBean.textColor, mPayColorBean.textColor );
			}
			//根据Path进行绘制，绘制五角星
			canvas.drawPath( path1, paint1 );
			path1.close();

			//定义一个Path对象，封闭一个多边形
			Path path2 = new Path();
			path2.moveTo( node.getX(), node.getY() );
			path2.lineTo( payNode2.getX(), payNode2.getY() );
			path2.lineTo( recNode2.getX(), recNode2.getY() );

			Paint paint2 = null;
			if ( payNode2.getY() > recNode2.getY() ) {
				paint2 = getPaint( mRecColorBean.textColor, mRecColorBean.textColor );
			} else {
				paint2 = getPaint( mPayColorBean.textColor, mPayColorBean.textColor );
			}
			//根据Path进行绘制，绘制五角星
			canvas.drawPath( path2, paint2 );
			path2.close();

		} else if ( payNode1.getX() == node.getX() ) {

			//定义一个Path对象，封闭一个多边形
			Path path = new Path();
			path.moveTo( node.getX(), node.getY() );
			path.lineTo( payNode2.getX(), payNode2.getY() );
			path.lineTo( recNode2.getX(), recNode2.getY() );

			Paint paint = null;
			if ( payNode2.getY() > recNode2.getY() ) {
				paint = getPaint( mRecColorBean.textColor, mRecColorBean.textColor );
			} else {
				paint = getPaint( mPayColorBean.textColor, mPayColorBean.textColor );
			}
			//根据Path进行绘制，绘制五角星
			canvas.drawPath( path, paint );
			path.close();
		} else if ( payNode2.getX() == node.getX() ) {
			//定义一个Path对象，封闭一个多边形
			Path path = new Path();
			path.moveTo( node.getX(), node.getY() );
			path.lineTo( payNode1.getX(), payNode1.getY() );
			path.lineTo( recNode1.getX(), recNode1.getY() );

			Paint paint = null;
			if ( payNode1.getY() > recNode1.getY() ) {
				paint = getPaint( mRecColorBean.textColor, mRecColorBean.textColor );
			} else {
				paint = getPaint( mPayColorBean.textColor, mPayColorBean.textColor );
			}
			//根据Path进行绘制，绘制五角星
			canvas.drawPath( path, paint );
			path.close();
		} else {
			Path path = new Path();
			path.moveTo( payNode1.getX(), payNode1.getY() );
			path.lineTo( recNode1.getX(), recNode1.getY() );
			path.lineTo( recNode2.getX(), recNode2.getY() );
			path.lineTo( payNode2.getX(), payNode2.getY() );

			Paint paint = null;
			if ( payNode1.getY() > recNode1.getY() ) {
				paint = getPaint( mRecColorBean.textColor, mRecColorBean.textColor );

			} else {
				paint = getPaint( mPayColorBean.textColor, mPayColorBean.textColor );

			}
			//根据Path进行绘制，绘制五角星
			canvas.drawPath( path, paint );
			path.close();
		}


	}

	private Paint getPaint( String deepColorStr, String shallColorStr ) {
		LogUtils.iTag( TAG, "deepColorStr:" + deepColorStr + " shallColorStr:" + shallColorStr );
		Paint linePaint = new Paint();
		linePaint.setAntiAlias( true );
		linePaint.setAlpha( 80 );
		//设置线条宽度
		linePaint.setStyle( Paint.Style.FILL );
		int deepColor, shallColor;
		try {
			deepColor = Color.parseColor( deepColorStr );
		} catch ( Exception e ) {
			LogUtils.eTag( TAG, e.getMessage(), e );
			deepColor = Color.parseColor( "#FEDFC6" );
		}
		try {
			shallColor = Color.parseColor( shallColorStr );
		} catch ( Exception e ) {
			LogUtils.eTag( TAG, e.getMessage(), e );
			shallColor = Color.parseColor( "#FEDFC6" );
		}
//        linePaint.setColor(deepColor);

		//渐变的是一个颜色序列(#faf84d,#003449,#808080,#cc423c)
		LinearGradient mShader = new LinearGradient( 0, 0, 0, 900, new int[]{ deepColor, shallColor }, null, Shader.TileMode.MIRROR );
		linePaint.setShader( mShader );
		return linePaint;
	}

	@Override
	protected void onLayout( boolean changed, int left, int top, int right, int bottom ) {
		super.onLayout( changed, left, top, right, bottom );
		LogUtils.iTag( TAG, "onLayout" );

	}

	@Override
	protected void onMeasure( int widthMeasureSpec, int heightMeasureSpec ) {
		super.onMeasure( widthMeasureSpec, heightMeasureSpec );
		LogUtils.iTag( TAG, "onMeasure" );

		int widthMode = MeasureSpec.getMode( widthMeasureSpec );
		int heightMode = MeasureSpec.getMode( heightMeasureSpec );
		int widthSize = MeasureSpec.getSize( widthMeasureSpec );
		int heightSize = MeasureSpec.getSize( heightMeasureSpec );

		if ( widthMode == MeasureSpec.EXACTLY ) {
			mWidth = widthSize;
		} else if ( widthMode == MeasureSpec.AT_MOST ) {
//            throw new IllegalArgumentException("width must be EXACTLY,you should set like
// android:width=\"200dp\"");
		}

		if ( heightMode == MeasureSpec.EXACTLY ) {
			mHeight = heightSize;
		} else if ( widthMeasureSpec == MeasureSpec.AT_MOST ) {

			throw new IllegalArgumentException( "height must be EXACTLY,you should set like " + "android:height=\"200dp\"" );
		}

		setMeasuredDimension( mWidth, mHeight );
	}
}
