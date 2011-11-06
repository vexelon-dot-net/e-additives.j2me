using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using System.Globalization;

namespace etata_database_gui
{
    public partial class frmHeaderInfo : Form
    {
        public frmHeaderInfo()
        {
            InitializeComponent();
        }

        public string Author
        {
            get
            {
                return txtAuthor.Text;
            }
            set
            {
                txtAuthor.Text = value;
            }
        }

        public string Language
        {
            get
            {
                return txtLang.Text;
            }
            set
            {
                txtLang.Text = value;
            }
        }

        public string DateCreated
        {
            get
            {
                return txtCreated.Text;
            }
            set
            {
                txtCreated.Text = value;
            }
        }

        public string DateLastChange
        {
            set
            {
                txtLastChange.Text = value;
            }
        }

        private void frmHeaderInfo_Load(object sender, EventArgs e)
        {
            this.Text = "Database header info";
            txtLastChange.Enabled = false;
            txtCreated.ReadOnly = true;
        }

        private void btnCancel_Click(object sender, EventArgs e)
        {
            this.DialogResult = DialogResult.Cancel;
            this.Close();
        }

        private void btnOk_Click(object sender, EventArgs e)
        {
            // validate date
            try
            {
                string date = Convert.ToDateTime(txtCreated.Text.Trim()).ToString(new CultureInfo("en-US"));
                DateCreated = date;
                this.DialogResult = DialogResult.OK;
                this.Close();
            }
            catch (Exception ex)
            {
                MessageBox.Show(ex.Message, "Error in date format", MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
        }

        private void dateTimePicker1_ValueChanged(object sender, EventArgs e)
        {
            txtCreated.Text = dateTimePicker1.Value.ToString();
        }

    }
}
