using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using System.IO;

namespace etata_database_gui
{
    public partial class Form1 : Form
    {
        private const string APP_TITLE = "E-additives Database Management";
        private int lastIndex = -1;

        public Form1()
        {
            InitializeComponent();
        }

        private string status
        {
            set
            {
                toolStripStatusLabel1.Text = value;
            }
        }

        private void Form1_Load(object sender, EventArgs e)
        {
            this.Text = string.Format("{0} - V{1}", APP_TITLE, Application.ProductVersion);
            //this.btnSave.Hide();
            status = "Ready.";

            // disable inital save menus
            saveToolStripMenuItem.Enabled = false;
            saveAsToolStripMenuItem.Enabled = false;
            exportToolStripMenuItem.Enabled = false;
            insertToolStripMenuItem.Enabled = false;

            try
            {
                //openFile(Path.Combine(Application.StartupPath, "en.xml"));
            }
            catch (Exception ex)
            {
                showError(ex);
            }
        }

        /// <summary>
        /// Open New Xml Database
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void openToolStripMenuItem_Click(object sender, EventArgs e)
        {
            try
            {
                openFileDialog1.Title = "Choose language file";
                openFileDialog1.Filter = "Xml database files (*.xml)|*.xml";
                openFileDialog1.InitialDirectory = Application.StartupPath;
                openFileDialog1.FileName = string.Empty;
                if (openFileDialog1.ShowDialog() == DialogResult.OK)
                    openFile(openFileDialog1.FileName);
            }
            catch (Exception ex)
            {
                showError(ex);
            }
        }

        /// <summary>
        /// Open file action
        /// </summary>
        /// <param name="path"></param>
        private void openFile(string path)
        {
            status = "Loading " + Path.GetFileName(path) + " ...";
            MainProc.getInstance().openDatabase(path);
            MainProc.getInstance().updateListBox(this.lstItems);
            MainProc.getInstance().updateHeader(lblHeader);
            // select something
            if (lstItems.Items.Count > 0)
                lstItems.SelectedIndex = 0;
            status = "Loaded - " + Path.GetFileName(path);

            saveToolStripMenuItem.Enabled = true;
            saveAsToolStripMenuItem.Enabled = true;
            exportToolStripMenuItem.Enabled = true;
            insertToolStripMenuItem.Enabled = true;
            this.Text = string.Format("{0} - {1}", APP_TITLE, Path.GetFileName(path));
        }

        /// <summary>
        /// Save current file
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void saveToolStripMenuItem_Click(object sender, EventArgs e)
        {
            try
            {
                saveFile();
            }
            catch (Exception ex)
            {
                showError(ex);
            }
        }

        /// <summary>
        /// Save filea action
        /// </summary>
        /// <param name="filename"></param>
        private void saveFile(string filename)
        {
            status = "Saving ...";

            saveAction(lastIndex);

            if (filename == null)
                MainProc.getInstance().saveDatabase();
            else
                MainProc.getInstance().saveDatabase(filename);

            MainProc.getInstance().reloadDatabase();
            MainProc.getInstance().updateListBox(this.lstItems);
            MainProc.getInstance().updateHeader(lblHeader);
            // select something
            if (lstItems.Items.Count > 0)
                lstItems.SelectedIndex = 0;
            status = "Saved.";
        }

        /// <summary>
        /// Save currently opened file action
        /// </summary>
        private void saveFile()
        {
            saveFile(null);
        }

        /// <summary>
        /// Save file Dialog
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void saveAsToolStripMenuItem_Click(object sender, EventArgs e)
        {
            try
            {
                saveFileDialog1.Title = "Choose save location";
                saveFileDialog1.Filter = "Xml database files (*.xml)|*.xml";
                if (saveFileDialog1.ShowDialog() == DialogResult.OK)
                {
                    saveFile(saveFileDialog1.FileName);
                    status = Path.GetFileName(saveFileDialog1.FileName) + " - saved.";
                }
            }
            catch (Exception ex)
            {
                showError(ex);
            }
        }

        /// <summary>
        /// Exit Application
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void exitToolStripMenuItem_Click(object sender, EventArgs e)
        {
            this.Close();
        }

        /// <summary>
        /// Confirm exit
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void Form1_FormClosing(object sender, FormClosingEventArgs e)
        {
            if (MainProc.getInstance().databaseChanged)
            {
                DialogResult dr = MessageBox.Show("File has not been saved !\nDo you want to save your changes before exit ? ", "Confirm", MessageBoxButtons.YesNoCancel, MessageBoxIcon.Question);
                if (dr == DialogResult.Cancel)
                {
                    e.Cancel = true;
                }
                else if (dr == DialogResult.Yes)
                {
                    saveFile();
                }
                else if (dr == DialogResult.No)
                {
                    //Exit
                }
            }
        }

        /// <summary>
        /// Add new string/text 
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void newTextToolStripMenuItem_Click(object sender, EventArgs e)
        {
            try
            {
                frmNewItem frmKey = new frmNewItem();
                if (frmKey.ShowDialog(this) == DialogResult.OK)
                {
                    string key = frmKey.getKeyName.Trim();
                    if ( MainProc.getInstance().isStringExists(key) )
                        throw new Exception("String '" + key + "' already exists !");

                    txtText.Clear();
                    MainProc.getInstance().saveStringProps(key, txtText);
                    MainProc.getInstance().updateListBox(lstItems);
                    foreach (Object item in lstItems.Items)
                    {
                        if ((string)item == key)
                        {
                            lstItems.SelectedItem = item;
                            break;
                        }
                    }
                }
            }
            catch (Exception ex)
            {
                showError("Error adding item ! (Details:" + ex.Message + ")");
            }
        }

        /// <summary>
        /// Add new additive item
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void newAdditiveToolStripMenuItem_Click(object sender, EventArgs e)
        {
            try
            {
                frmNewItem frmKey = new frmNewItem();
                if (frmKey.ShowDialog(this) == DialogResult.OK)
                {
                    string key = frmKey.getKeyName.Trim();

                    if (MainProc.getInstance().isItemExists(key))
                        throw new Exception("Item '" + key + "' already exists !");

                    txtName.Clear(); txtStatus.Clear(); txtFunction.Clear(); txtFoundIn.Clear(); txtSideFx.Clear(); txtDetails.Clear();
                    MainProc.getInstance().saveItemProps(key, txtName, txtStatus, txtFunction, txtVegetarians, txtFoundIn, txtSideFx, txtDetails);
                    MainProc.getInstance().updateListBox(lstItems);
                    foreach (Object item in lstItems.Items)
                    {
                        if ((string)item == key)
                        {
                            lstItems.SelectedItem = item;
                            break;
                        }
                    }
                }
            }
            catch (Exception ex)
            {
                showError("Error adding item ! (Details: " + ex.Message + " )");
            }
        }

        /// <summary>
        /// Update items list
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void txtSearch_TextChanged(object sender, EventArgs e)
        {
            MainProc.getInstance().updateListBox(this.lstItems, txtSearch.Text);
        }

        /// <summary>
        /// Remove default search string
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void txtSearch_Click(object sender, EventArgs e)
        {
            if (txtSearch.Text == "[Search]")
                txtSearch.Text = "";
        }

        /// <summary>
        /// Show properties of selected string or item
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void lstItems_SelectedIndexChanged(object sender, EventArgs e)
        {
            // was there anything selected before ? save, if so !
            //saveAction(lastIndex);

            ListBox lstBox = (ListBox)sender;
            String key = (String)lstBox.Items[lstBox.SelectedIndex];
            lastIndex = lstBox.SelectedIndex;

            status = String.Format("Item '{0}' selected", key);
            if (MainProc.getInstance().isStringItem(key))
            {
                // hide unused Item controls
                panelItem.Hide();
                lblText.Show(); txtText.Show();
                MainProc.getInstance().viewStringProps(key, txtText);
            }
            else
            {
                // hide unused Text controls
                panelItem.Show();
                lblText.Hide(); txtText.Hide();
                MainProc.getInstance().viewItemProps(key,
                    txtNumber, txtName, txtStatus, txtFunction, txtVegetarians, txtFoundIn, txtSideFx, txtDetails);
            }
        }

        /// <summary>
        /// Save properties of currently selected item
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void btnSave_Click(object sender, EventArgs e)
        {
            saveAction(lstItems.SelectedIndex);
        }

        /// <summary>
        /// Actual save of item (used for autosaves)
        /// </summary>
        private void saveAction(int index)
        {
            // check deleted
            if (!MainProc.getInstance().isItemSelected(index))
            {
                showError("No item selected!");
                return;
            }

            string key = (String)lstItems.Items[index];
            status = "Saving item...";
            if (MainProc.getInstance().isStringItem(key))
            {
                MainProc.getInstance().saveStringProps(key, txtText);
            }
            else
            {
                MainProc.getInstance().saveItemProps(key, txtName, txtStatus, txtFunction, txtVegetarians, txtFoundIn, txtSideFx, txtDetails);
            }
            status = String.Format("Item '{0}' saved.", key);

        }

        /// <summary>
        /// A key was pressed on the List
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void lstItems_KeyUp(object sender, KeyEventArgs e)
        {
            // remove item
            if (e.KeyCode == Keys.Delete)
                deleteItem();
        }

        /// <summary>
        /// Show error message box
        /// </summary>
        /// <param name="message"></param>
        private void showError(string message)
        {
            status = "Error !";
            MessageBox.Show(message, "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
        }

        /// <summary>
        /// Show error message box given exception msg
        /// </summary>
        /// <param name="ex"></param>
        private void showError(Exception ex)
        {
            showError(ex.ToString());
        }

        /// <summary>
        /// Remove item menu action
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void deleteToolStripMenuItem_Click(object sender, EventArgs e)
        {
            deleteItem();
        }

        /// <summary>
        /// Remove item actual action
        /// </summary>
        private void deleteItem()
        {
            ListBox lstBox = lstItems;
            // check deleted
            if (!MainProc.getInstance().isItemSelected(lstBox.SelectedIndex))
            {
                showError("No item selected!");
                return;
            }
            String key = (String)lstBox.Items[lstBox.SelectedIndex];

            MainProc.getInstance().deleteItemOrString(key);
            MainProc.getInstance().updateListBox(lstItems, txtSearch.Text);
            status = "Item '" + key + "' deleted.";
        }

        /// <summary>
        /// Export special version of the xml db (CSV)
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void exportToolStripMenuItem_Click(object sender, EventArgs e)
        {
            try
            {
                saveFileDialog1.Title = "Choose save location";
                saveFileDialog1.Filter = "CSV files (*.csv)|*.csv";
                if (saveFileDialog1.ShowDialog() == DialogResult.OK)
                {
                    saveFile();
                    MainProc.getInstance().exportDatabaseToCSV(saveFileDialog1.FileName);
                    status = Path.GetFileName(saveFileDialog1.FileName) + " - saved.";
                }
            }
            catch (Exception ex)
            {
                showError(ex);
            }
        }

        /// <summary>
        /// Change database header info
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void lblHeader_DoubleClick(object sender, EventArgs e)
        {
            frmHeaderInfo frmHI = new frmHeaderInfo();
            string lang = string.Empty,
                auth = string.Empty,
                created = string.Empty,
                changed = string.Empty;
            MainProc.getInstance().getDatabaseProperties(ref lang, ref auth, ref created, ref changed);
            frmHI.Language = lang;
            frmHI.Author = auth;
            frmHI.DateCreated = created;
            frmHI.DateLastChange = changed;

            if (DialogResult.OK == frmHI.ShowDialog())
            {
                MainProc.getInstance().setDatabaseProperties(frmHI.Language, frmHI.Author, frmHI.DateCreated);
                MainProc.getInstance().updateHeader(lblHeader);
            }
        }

        /// <summary>
        /// Save currently selected item
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void saveItemToolStripMenuItem_Click(object sender, EventArgs e)
        {
            saveAction(lstItems.SelectedIndex);
        }

        /// <summary>
        /// Show "About" message box
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void aboutToolStripMenuItem1_Click(object sender, EventArgs e)
        {
            System.IO.StringWriter strw = new StringWriter();
            strw.WriteLine("E-additives Database Management");
            strw.WriteLine("(c) Copyright 2010 - Petar Petrov");
            strw.WriteLine();
            strw.WriteLine("Build " + Application.ProductVersion);
            strw.Close(); 
            MessageBox.Show(strw.ToString(), "About", MessageBoxButtons.OK, MessageBoxIcon.Information );
        }

        /// <summary>
        /// Re-calculate controls sizes
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void Form1_SizeChanged(object sender, EventArgs e)
        {
            lstItems.Height = statusStrip1.Top - lstItems.Top - 5;
            groupBox1.Height = statusStrip1.Top - groupBox1.Top - 5;
            groupBox1.Width = statusStrip1.Width - groupBox1.Left - 5;
        }

        /// <summary>
        /// Merge one or more tags or attribs from current file with another language file
        /// </summary>
        /// <param name="sender"></param>
        /// <param name="e"></param>
        private void mergeWithToolStripMenuItem_Click(object sender, EventArgs e)
        {
            try
            {
                openFileDialog1.Title = "Choose source language file";
                openFileDialog1.Filter = "Xml database files (*.xml)|*.xml";
                openFileDialog1.InitialDirectory = Application.StartupPath;
                openFileDialog1.FileName = string.Empty;
                if (openFileDialog1.ShowDialog() == DialogResult.OK)
                    mergeWith(openFileDialog1.FileName);
            }
            catch (Exception ex)
            {
                showError(ex);
            }
        }

        public void mergeWith(string path)
        {
            if (MainProc.getInstance().databaseChanged)
            {
                DialogResult dr = MessageBox.Show("Changes might be lost!\nDo you want to continue ? ", "Confirm", MessageBoxButtons.OKCancel, MessageBoxIcon.Question);
                if (dr == DialogResult.Cancel)
                {
                    return;
                }
                else if (dr == DialogResult.OK)
                {

                }
            }

            status = "Merging with " + Path.GetFileName(path) + " ...";

            frmMerge formMerge = new frmMerge();
            if (formMerge.ShowDialog() == DialogResult.Cancel)
                return;

            MainProc.getInstance().mergeWith(path, formMerge.merges);

            // select something
            if (lstItems.Items.Count > 0)
                lstItems.SelectedIndex = 0;
            status = "Merged.";

            // update box
            MainProc.getInstance().updateListBox(lstItems);

            saveToolStripMenuItem.Enabled = true;
            saveAsToolStripMenuItem.Enabled = true;
            exportToolStripMenuItem.Enabled = true;
            insertToolStripMenuItem.Enabled = true;
        }

 
    }
}