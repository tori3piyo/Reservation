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
            res ="�N�����ɂ͐������w�肵�Ă�������";
            return res;
        }
        res = facility + " �\���\n\n";

       
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
                    res = "�\��͂���܂���";
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
            frame.buttonLog.setLabel(" ���O�C�� "); 
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
                        frame.buttonLog.setLabel("���O�A�E�g");
                        res = "";
                    }else {
                        
                        res = "���O�C���ł��܂���BID�A�p�X���[�h���Ⴂ�܂��B";
                    }
                } else { 
                    res = "���O�C���ł��܂���BID�A�p�X���[�h���Ⴂ�܂��B";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return res;
    }
    private boolean checkReservationDate( int y, int m, int d){
        // �\���
        Calendar dateR = Calendar.getInstance();
        dateR.set( y, m-1, d);    // ������1�����Ȃ���΂Ȃ�Ȃ����Ƃɒ��ӁI

        // �����̂P����
        Calendar date1 = Calendar.getInstance();
        date1.add(Calendar.DATE, 1);

        // �����̂R������i90����)
        Calendar date2 = Calendar.getInstance();
        date2.add(Calendar.DATE, 90);

        if ( dateR.after(date1) && dateR.before(date2)){
            return true;
        }
        return false;
    }
    public String makeReservation(Reservation_view frame){

        String res="";        //���ʂ�����ϐ�

        if ( flagLogin){ // ���O�C�����Ă����ꍇ
            //�V�K�\���ʍ쐬
            ReservationDialog rd = new ReservationDialog(frame);

            // �V�K�\���ʂ̗\����ɁC���C����ʂɐݒ肳��Ă���N������ݒ肷��
            rd.tfYear.setText(frame.tfYear.getText());
            rd.tfMonth.setText(frame.tfMonth.getText());
            rd.tfDay.setText(frame.tfDay.getText());

            // �V�K�\���ʂ�����
            rd.setVisible(true);
            if ( rd.canceled){
                return res;
            }
            try {
                //�V�K�\���ʂ���N�������擾
                String ryear_str = rd.tfYear.getText();
                String rmonth_str = rd.tfMonth.getText();
                String rday_str = rd.tfDay.getText();

                // �N�������������ǂ��������`�F�b�N���鏈��
                int ryear = Integer.parseInt( ryear_str);
                int rmonth = Integer.parseInt( rmonth_str);
                int rday = Integer.parseInt( rday_str);

                if ( checkReservationDate( ryear, rmonth, rday)){    // ���Ԃ̏����𖞂����Ă���ꍇ
                    // �V�K�\���ʂ���{�ݖ��C�J�n�����C�I���������擾
                    String facility = rd.choiceFacility.getSelectedItem();
                    String st = rd.startHour.getSelectedItem()+":" + rd.startMinute.getSelectedItem() +":00";
                    String et = rd.endHour.getSelectedItem() + ":" + rd.endMinute.getSelectedItem() +":00";

                    if( st.equals(et)){        //�J�n�����ƏI��������������
                        res = "�J�n�����ƏI�������������ł�";
                    } else {


                        try {
                            // ���Ɠ����ꌅ��������C�O��0�����鏈��
                            if (rmonth_str.length()==1) {
                                rmonth_str = "0" + rmonth_str;
                            }
                            if ( rday_str.length()==1){
                                rday_str = "0" + rday_str;
                            }
                            //(2) MySQL�̑���(SELECT���̎��s)
                            String rdate = ryear_str + "-" + rmonth_str + "-" + rday_str;
                              // �w�肵���{�݂̎w�肵���\����̗\������擾����N�G��
                              // �N�G���[�����s���Č��ʂ̃Z�b�g���擾
                              ResultSet rs = mysql.selectReservation(rdate, facility);
                              // �������ʂɑ΂��ďd�Ȃ�`�F�b�N�̏���
                              boolean ng = false;    //�d�Ȃ�`�F�b�N�̌��ʂ̏����l�i�d�Ȃ��Ă��Ȃ�=false�j��ݒ�
                              // �擾�������R�[�h���ɑ΂��Ċm�F
                              while(rs.next()){
                                      //���R�[�h�̊J�n�����ƏI�����������ꂼ��start��end�ɐݒ�
                                    String start = rs.getString("start_time");
                                    String end = rs.getString("end_time");

                                    if ( (start.compareTo(st)<0 && st.compareTo(end)<0) ||        //���R�[�h�̊J�n�������V�K�̊J�n�����@AND�@�V�K�̊J�n���������R�[�h�̏I������
                                         (st.compareTo(start)<0 && start.compareTo(et)<0)){        //�V�K�̊J�n���������R�[�h�̊J�n�����@AND�@���R�[�h�̊J�n�������V�K�̊J�n����
                                             // �d���L��̏ꍇ�� ng ��true�ɐݒ�
                                        ng = true; break;
                                    }
                              }
                              /// �d�Ȃ�`�F�b�N�̏����@�����܂�  ///////

                              if (!ng){    //�d�Ȃ��Ă��Ȃ��ꍇ
                                  int rs_int = mysql.insertReservation(rdate, facility, st, et, reservation_userid);
                                  res ="�\�񂳂�܂���";
                              } else {    //�d�Ȃ��Ă����ꍇ
                                  res = "���ɂ���\��ɏd�Ȃ��Ă��܂�";
                              }
                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    res = "�\����������ł��D";
                }
            } catch(NumberFormatException e){
                res ="�\����ɂ͐������w�肵�Ă�������";
            }
        } else { // ���O�C�����Ă��Ȃ��ꍇ
            res = "���O�C�����Ă�������";
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
					res = "�\�񂪂���܂���";
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		//���O�C�����Ă��Ȃ��Ƃ�
		}else{
			res = "���O�C�����Ă�������";
		}
		return res;
	}
	
	public String deleteReserv(){
		String res = "";
		MySQL mysql = new MySQL();
		//���O�C�����Ă���Ƃ�
		if(flagLogin){ 
			try {
				res = confirmReserv();
				int rs = mysql.deleteReserv(reservation_userid);
				if(rs != 0){
					res += "��L�̗\����������܂���";
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		//���O�C�����Ă��Ȃ��Ƃ�
		}else{ 
			res = "���O�C�����Ă�������";
		}
		return res;

	}
}
