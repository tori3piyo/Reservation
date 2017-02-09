package reservation;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class LoginDialog extends Dialog implements ActionListener,WindowListener{
	
	boolean canceled;        
    TextField tfUserID;       
    TextField tfPassword;    
    Button buttonOK;        
    Button buttonCancel;    
    Panel panelNorth;        
    Panel panelMid;           
    Panel panelSouth;    
    public LoginDialog(Frame owner) {
        //���N���X(Dialog)�̃R���X�g���N�^���Ăяo��
        super(owner,"Login",true);
        //�L�����Z���͏����l�ł�true�Ƃ��Ă���
        canceled = true;

        // �e�L�X�g�t�B�[���h�̐���
        tfUserID = new TextField("",10);
        tfPassword = new TextField("",10);
        // �p�X���[�h����͂���e�L�X�g�t�B�[���h�͓��͂��������� * �ɂȂ�悤�ɂ���
        tfPassword.setEchoChar('*');

        // �{�^���̐���
        buttonOK = new Button("OK");
        buttonCancel = new Button("�L�����Z��");

        // �p�l���̐���
        panelNorth = new Panel();
        panelMid = new Panel();
        panelSouth = new Panel();

        // �㕔�p�l����,���[�UID�e�L�X�g�t�B�[���h��ǉ�
        panelNorth.add( new Label("���[�UID"));
        panelNorth.add(tfUserID);

        // �����p�l����,�p�X���[�h�e�L�X�g�t�B�[���h��ǉ�
        panelMid.add( new Label("�p�X���[�h"));
        panelMid.add(tfPassword);

        // �����p�l����2�̃{�^����ǉ�
        panelSouth.add(buttonCancel);
        panelSouth.add(buttonOK);

        // LoginDialog��BorderLayout�ɐݒ肵,3�̃p�l����ǉ�
        setLayout( new BorderLayout());
        add( panelNorth,BorderLayout.NORTH);
        add( panelMid, BorderLayout.CENTER);
        add( panelSouth, BorderLayout.SOUTH);

        // �E�B���h�E���X�i��ǉ�
        addWindowListener(this);

        // �{�^���ɃA�N�V�������X�i��ǉ�
        buttonOK.addActionListener(this);
        buttonCancel.addActionListener(this);

        // �傫���̐ݒ�,�E�B���h�E�̃T�C�Y�ύX�s�̐ݒ�
        this.setBounds( 100, 100, 350, 150);
        setResizable(false);

    }
	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowClosing(WindowEvent arg0) {
		// TODO Auto-generated method stub
		setVisible(false);
		canceled = true;
		dispose();
	}
	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if ( e.getSource() == buttonCancel){
            canceled = true;
        } else if ( e.getSource() == buttonOK){
            canceled = false;
        }
        setVisible(false);
        dispose();

	}


}
