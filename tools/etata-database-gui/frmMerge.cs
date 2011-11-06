using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace etata_database_gui
{
    public partial class frmMerge : Form
    {

        public int merges = 0;
        private string[] _items = new string[XmlDatabase.COUNT_ATTRIBS];

        public frmMerge()
        {
            InitializeComponent();
        }

        private void frmMerge_Load(object sender, EventArgs e)
        {
            _items[XmlDatabase.ATTRIB_DANGERLEVEL] = "status";
            _items[XmlDatabase.ATTRIB_DETAILS] = "info";
            _items[XmlDatabase.ATTRIB_FOOD] = "food";
            _items[XmlDatabase.ATTRIB_FUNCTION] = "function";
            _items[XmlDatabase.ATTRIB_NAME] = "name";
            _items[XmlDatabase.ATTRIB_SIDEFX] = "warn";
            _items[XmlDatabase.ATTRIB_VEGETARIANS] = "veg";

            clbMergeTypes.Items.Clear();
            clbMergeTypes.Items.AddRange(_items);
        }

        private void btnOk_Click(object sender, EventArgs e)
        {
            foreach (string item in clbMergeTypes.CheckedItems)
            {
                    if (_items[XmlDatabase.ATTRIB_DANGERLEVEL].Equals(item))
                        merges |= XmlDatabase.mergeDangerLevel;

                    if (_items[XmlDatabase.ATTRIB_DETAILS].Equals(item))
                        merges |= XmlDatabase.mergeDetails;

                    if (_items[XmlDatabase.ATTRIB_FOOD].Equals(item))
                        merges |= XmlDatabase.mergeFood;

                    if ( _items[XmlDatabase.ATTRIB_FUNCTION].Equals(item))
                        merges |= XmlDatabase.mergeFunction;

                    if ( _items[XmlDatabase.ATTRIB_NAME].Equals(item))
                        merges |= XmlDatabase.mergeName;

                    if (_items[XmlDatabase.ATTRIB_SIDEFX].Equals(item))
                        merges |= XmlDatabase.mergeSideFx;

                    if (_items[XmlDatabase.ATTRIB_VEGETARIANS].Equals(item))
                        merges |= XmlDatabase.mergeVegetarians;
            }

            this.DialogResult = DialogResult.OK;
        }

        private void btnCancel_Click(object sender, EventArgs e)
        {
            this.DialogResult = DialogResult.Cancel;
            this.Close();
        }
    }
}
