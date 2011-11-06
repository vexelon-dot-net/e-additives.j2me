using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace etata_database_gui
{
    public partial class frmNewItem : Form
    {
        public frmNewItem()
        {
            InitializeComponent();
        }

        public string getKeyName
        {
            get
            {
                return textBox1.Text;
            }
        }

        private void textBox1_MouseClick(object sender, MouseEventArgs e)
        {
            //clear
            textBox1.Text = "";
        }

        private void textBox1_KeyUp(object sender, KeyEventArgs e)
        {
            if (e.KeyCode == Keys.Return)
            {
                this.DialogResult = DialogResult.OK;
                this.Close();
            }
        }

        private void btnOk_Click(object sender, EventArgs e)
        {
            this.DialogResult = DialogResult.OK;
            this.Close();
        }

        private void btnCancel_Click(object sender, EventArgs e)
        {
            this.DialogResult = DialogResult.Cancel;
            this.Close();
        }

        private void frmNewItem_Load(object sender, EventArgs e)
        {
            textBox1.Text = "[Enter text here]";
        }
    }
}
