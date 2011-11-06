using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;
using System.Drawing;

namespace etata_database_gui
{
    class MainProc
    {
        private static MainProc _instance = null;
        private XmlDatabase _database = null;
        private bool _any_db_changes = false;
        private string _db_path = string.Empty;

        protected MainProc()
        {
        }

        public static MainProc getInstance()
        {
            if (_instance == null)
                _instance = new MainProc();

            return _instance;
        }

        public bool databaseChanged
        {
            get
            {
                return _any_db_changes;
            }
        }

        public XmlDatabase db
        {
            get
            {
                return _database;
            }
        }

        public void openDatabase(string path)
        {
            if (_database != null)
            {
                _database = null;
                System.GC.Collect();
            }

            _db_path = path;
            _database = new XmlDatabase(_db_path);

            //if (null == _database)
            //    _database = new XmlDatabase(path);
            //else
            //    _database.load(path);
        }

        public void reloadDatabase()
        {
            openDatabase(_db_path);
        }

        public void saveDatabase(string path)
        {
            if (_database != null)
            {
                _database.save(path);
                _any_db_changes = false;
            }
        }

        public void saveDatabase()
        {
            if (_database != null)
            {
                _database.save();
                _any_db_changes = false;
            }
        }

        public void exportDatabaseToCSV(string path)
        {
            if (_database != null)
            {
                _database.saveAsCSV(path);
            }
        }

        public void mergeWith(string withPath, int merges)
        {
            _database.mergeByTagOrAttrib(withPath, merges);
        }

        public void updateHeader(Label label)
        {
            label.Text = String.Format("LANG:{0}, AUTHOR:{1}, CREATED:{2}, LASTCHANGE:{3}",
                _database.Language,
                _database.Author,
                _database.DateCreated,
                _database.DateLastChange
                );
        }

        public void getDatabaseProperties(ref string Language, ref string Author, ref string DateCreated, ref string DateLastChange)
        {
            Language = _database.Language;
            Author = _database.Author;
            DateCreated = _database.DateCreated;
            DateLastChange = _database.DateLastChange;
        }

        public void setDatabaseProperties(string Language, string Author, string DateCreated/*, string DateLastChange*/)
        {
            _database.Language = Language;
            _database.Author = Author;
            _database.DateCreated = DateCreated;
        }

        public bool isItemSelected(int idx)
        {
            return idx != -1;
        }

        public void updateListBox(ListBox listBox, string query)
        {
            if (_database == null)
                return;

            listBox.Items.Clear();
            if (query.Trim().Length == 0 || query.Trim() == "[Search]")
            {
                foreach (String key in _database.strings.Keys)
                {
                    if (!_database.isDeleted(key))
                        listBox.Items.Add(key);
                }
                foreach (String key in _database.items.Keys)
                {
                    if (!_database.isDeleted(key))
                        listBox.Items.Add(key);
                }
            }
            else
            {
                foreach (String key in _database.strings.Keys)
                {
                    if (key.ToLower().StartsWith(query.ToLower()))
                    {
                        if (!_database.isDeleted(key))
                            listBox.Items.Add(key);
                    }
                }
                foreach (String key in _database.items.Keys)
                {
                    if (key.ToLower().StartsWith(query.ToLower()))
                    {
                        if (!_database.isDeleted(key))
                            listBox.Items.Add(key);
                    }
                }
            }
        }

        public bool isItemExists(string key)
        {
            return _database.getItem(key) != null;
        }

        public bool isStringExists(string key)
        {
            return _database.getString(key) != null;
        }

        public void updateListBox(ListBox listBox)
        {
            this.updateListBox(listBox, "");
        }

        public bool isStringItem(String key)
        {
            return _database.getString(key) != null;
        }

        public void viewStringProps(String key, TextBox textBox)
        {
            String value = _database.getString(key);
            textBox.Text = value;
        }

        public void viewItemProps(String key,
            TextBox txtNumber, TextBox txtName, TextBox txtStatus, TextBox txtFunction, TextBox txtVegetarians, TextBox txtFood, TextBox txtSideFx, TextBox txtDetails)
        {
            txtNumber.Text = key;
            txtName.Text = _database.getItemValue(key, XmlDatabase.ATTRIBUTES.ATTRIB_NAME);
            txtStatus.Text = _database.getItemValue(key, XmlDatabase.ATTRIBUTES.ATTRIB_DANGERLEVEL);
            txtVegetarians.Text = _database.getItemValue(key, XmlDatabase.ATTRIBUTES.ATTRIB_VEGETARIANS);
            txtFunction.Text = _database.getItemValue(key, XmlDatabase.ATTRIBUTES.ATTRIB_FUNCTION);
            txtFood.Text = _database.getItemValue(key, XmlDatabase.ATTRIBUTES.ATTRIB_FOOD);
            txtSideFx.Text = _database.getItemValue(key, XmlDatabase.ATTRIBUTES.ATTRIB_SIDEFX);
            txtDetails.Text = _database.getItemValue(key, XmlDatabase.ATTRIBUTES.ATTRIB_DETAILS);
        }

        public void saveStringProps(String key, TextBox textBox)
        {
            _any_db_changes = true;
            _database.setString(key, textBox.Text.Trim());
        }

        public void saveItemProps(String key,
            TextBox txtName, TextBox txtStatus, TextBox txtFunction, TextBox txtVegetarians, TextBox txtFood, TextBox txtSideFx, TextBox txtDetails)
        {
            _any_db_changes = true;
            _database.setItemValue(key, XmlDatabase.ATTRIBUTES.ATTRIB_NAME, txtName.Text.Trim());
            _database.setItemValue(key, XmlDatabase.ATTRIBUTES.ATTRIB_DANGERLEVEL, txtStatus.Text.Trim());
            _database.setItemValue(key, XmlDatabase.ATTRIBUTES.ATTRIB_VEGETARIANS, txtVegetarians.Text.Trim());
            _database.setItemValue(key, XmlDatabase.ATTRIBUTES.ATTRIB_FUNCTION, txtFunction.Text.Trim());
            _database.setItemValue(key, XmlDatabase.ATTRIBUTES.ATTRIB_FOOD, txtFood.Text.Trim());
            _database.setItemValue(key, XmlDatabase.ATTRIBUTES.ATTRIB_SIDEFX, txtSideFx.Text.Trim());
            _database.setItemValue(key, XmlDatabase.ATTRIBUTES.ATTRIB_DETAILS, txtDetails.Text.Trim());
        }

        public void deleteItemOrString(String key)
        {
            _database.delete(key);
        }

        public void undeleteItemOrString(String key)
        {
            _database.undelete(key);
        }
    }
}
