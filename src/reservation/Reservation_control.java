package reservation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.awt.Dialog;

public class Reservation_control {
	
	MySQL mysql;
	Statement sqlStmt;
	String reservation_userid;
    private boolean flagLogin;

	public Reservation_control(){
		this.mysql = new MySQL();
		flagLogin = false;
	}

	
    public String getReservationOn( String facility, String ryear_str, String rmonth_str, String rday_str){
        String res = "";
       
        try {
            int ryear = Integer.parseInt( ryear_str);
            int rmonth = Integer.parseInt( rmonth_str);
            int rday = Integer.parseInt( rday_str);
        } catch(NumberFormatException e){
            res ="年月日には数字を指定してください";
            return res;
        }
        res = facility + " 予約状況\n\n";

       
        if (rmonth_str.length()==1) {
            rmonth_str = "0" + rmonth_str;
        }
        if ( rday_str.length()==1){
            rday_str = "0" + rday_str;
        }
        
        String rdate = ryear_str + "-" + rmonth_str + "-" + rday_str;

       
        try {
      
            ResultSet rs = mysql.selectReservation(rdate, facility); 
                boolean exist = false;
                while(rs.next()){
                    String start = rs.getString("start_time");
                    String end = rs.getString("end_time");
                    res += " " + start + " -- " + end + "\n";
                    exist = true;
                }

                if ( !exist){
                    res = "予約はありません";
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            return res;
        }
    public String loginLogout( Reservation_view frame){
        String res=""; 
        if ( flagLogin){ 
            flagLogin = false;
            frame.buttonLog.setLabel(" ログイン "); 
        } else {
            
            LoginDialog ld = new LoginDialog(frame);
            ld.setVisible(true);
            ld.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
            
            if ( ld.canceled){
                return "";
            }

           
            reservation_userid = ld.tfUserID.getText();
           
            String password = ld.tfPassword.getText();

            
            try { 
                ResultSet rs = mysql.selectUser(reservation_userid); 
                if (rs.next()){
                    rs.getString("password");
                    String password_from_db = rs.getString("password");
                    if ( password_from_db.equals(password)){
                        flagLogin = true;
                        frame.buttonLog.setLabel("ログアウト");
                        res = "";
                    }else {
                        
                        res = "ログインできません。ID、パスワードが違います。";
                    }
                } else { 
                    res = "ログインできません。ID、パスワードが違います。";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return res;
    }
    private boolean checkReservationDate( int y, int m, int d){
        // 予約日
        Calendar dateR = Calendar.getInstance();
        dateR.set( y, m-1, d);    // 月から1引かなければならないことに注意！

        // 今日の１日後
        Calendar date1 = Calendar.getInstance();
        date1.add(Calendar.DATE, 1);

        // 今日の３ヶ月後（90日後)
        Calendar date2 = Calendar.getInstance();
        date2.add(Calendar.DATE, 90);

        if ( dateR.after(date1) && dateR.before(date2)){
            return true;
        }
        return false;
    }
    public String makeReservation(Reservation_view frame){

        String res="";        //結果を入れる変数

        if ( flagLogin){ // ログインしていた場合
            //新規予約画面作成
            ReservationDialog rd = new ReservationDialog(frame);

            // 新規予約画面の予約日に，メイン画面に設定されている年月日を設定する
            rd.tfYear.setText(frame.tfYear.getText());
            rd.tfMonth.setText(frame.tfMonth.getText());
            rd.tfDay.setText(frame.tfDay.getText());

            // 新規予約画面を可視化
            rd.setVisible(true);
            if ( rd.canceled){
                return res;
            }
            try {
                //新規予約画面から年月日を取得
                String ryear_str = rd.tfYear.getText();
                String rmonth_str = rd.tfMonth.getText();
                String rday_str = rd.tfDay.getText();

                // 年月日が数字かどうかををチェックする処理
                int ryear = Integer.parseInt( ryear_str);
                int rmonth = Integer.parseInt( rmonth_str);
                int rday = Integer.parseInt( rday_str);

                if ( checkReservationDate( ryear, rmonth, rday)){    // 期間の条件を満たしている場合
                    // 新規予約画面から施設名，開始時刻，終了時刻を取得
                    String facility = rd.choiceFacility.getSelectedItem();
                    String st = rd.startHour.getSelectedItem()+":" + rd.startMinute.getSelectedItem() +":00";
                    String et = rd.endHour.getSelectedItem() + ":" + rd.endMinute.getSelectedItem() +":00";

                    if( st.equals(et)){        //開始時刻と終了時刻が等しい
                        res = "開始時刻と終了時刻が同じです";
                    } else {


                        try {
                            // 月と日が一桁だったら，前に0をつける処理
                            if (rmonth_str.length()==1) {
                                rmonth_str = "0" + rmonth_str;
                            }
                            if ( rday_str.length()==1){
                                rday_str = "0" + rday_str;
                            }
                            //(2) MySQLの操作(SELECT文の実行)
                            String rdate = ryear_str + "-" + rmonth_str + "-" + rday_str;
                              // 指定した施設の指定した予約日の予約情報を取得するクエリ
                              // クエリーを実行して結果のセットを取得
                              ResultSet rs = mysql.selectReservation(rdate, facility);
                              // 検索結果に対して重なりチェックの処理
                              boolean ng = false;    //重なりチェックの結果の初期値（重なっていない=false）を設定
                              // 取得したレコード一つ一つに対して確認
                              while(rs.next()){
                                      //レコードの開始時刻と終了時刻をそれぞれstartとendに設定
                                    String start = rs.getString("start_time");
                                    String end = rs.getString("end_time");

                                    if ( (start.compareTo(st)<0 && st.compareTo(end)<0) ||        //レコードの開始時刻＜新規の開始時刻　AND　新規の開始時刻＜レコードの終了時刻
                                         (st.compareTo(start)<0 && start.compareTo(et)<0)){        //新規の開始時刻＜レコードの開始時刻　AND　レコードの開始時刻＜新規の開始時刻
                                             // 重複有りの場合に ng をtrueに設定
                                        ng = true; break;
                                    }
                              }
                              /// 重なりチェックの処理　ここまで  ///////

                              if (!ng){    //重なっていない場合
                                  int rs_int = mysql.insertReservation(rdate, facility, st, et, reservation_userid);
                                  res ="予約されました";
                              } else {    //重なっていた場合
                                  res = "既にある予約に重なっています";
                              }
                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    res = "予約日が無効です．";
                }
            } catch(NumberFormatException e){
                res ="予約日には数字を指定してください";
            }
        } else { // ログインしていない場合
            res = "ログインしてください";
        }
        return res;
    }

	public String FacilityInfo(){
		String res = "";
		MySQL mysql = new MySQL();
		try {
			ResultSet rs = mysql.FacilityInfo();
			while (rs.next()) {
				String expl = rs.getString("Explanation");
				res += expl + "\n";
			} 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
	
	public String confirmReserv(){
		String res = "";
		MySQL mysql = new MySQL();

		if(flagLogin){
			try{
				ResultSet rs = mysql.confirmReserv(reservation_userid);
				boolean exist = false;
				while(rs.next()){
					String user = rs.getString("user_id");
					String facility = rs.getString("facility_name");
					String date = rs.getString("date");
					String start = rs.getString("start_time");
					String end = rs.getString("end_time");
					res += "user: "+ user +"   "+ facility + "   "+ date +"   "+ start +" -- "+ end +"\n";
					exist = true;
				}
				if(!exist){
					res = "予約がありません";
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		//ログインしていないとき
		}else{
			res = "ログインしてください";
		}
		return res;
	}
	
	public String deleteReserv(){
		String res = "";
		MySQL mysql = new MySQL();
		//ログインしているとき
		if(flagLogin){ 
			try {
				res = confirmReserv();
				int rs = mysql.deleteReserv(reservation_userid);
				if(rs != 0){
					res += "上記の予約を消去しました";
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		//ログインしていないとき
		}else{ 
			res = "ログインしてください";
		}
		return res;

	}
}
