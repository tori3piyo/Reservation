package reservation;

public class Reservation_main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Reservation_control rcontrol = new Reservation_control();
		Reservation_view rview = new Reservation_view(rcontrol);
		rview.setBounds(2,2,700,500);
		rview.setVisible(true);
	}

}
