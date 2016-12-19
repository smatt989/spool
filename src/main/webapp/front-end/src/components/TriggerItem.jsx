import React from 'react';
import { render } from 'react-dom';
import PureRenderMixin from 'react-addons-pure-render-mixin';
import {connect} from 'react-redux';
import {Modal, Button, FormControl, Panel, Accordion, ButtonGroup, DropdownButton, MenuItem, InputGroup, Form, Col, Row} from 'react-bootstrap';
import {VariableFormContainer} from './VariableForm'
import {showProps} from '../utilities';
import {updateItemOfStagedTrigger, removeActionFromStagedTrigger} from '../action_creators';
import _ from 'lodash';

const TriggerItem = React.createClass({
  mixins: [PureRenderMixin],
  getOptions: function() {
    return this.props.options || [];
  },
  getItem: function() {
    return this.props.item || null;
  },
  itemSubTypeById: function(id){
    return _.find(this.getOptions(), function(o){return o.id === parseInt(id)})
  },
  arityIsMany: function(){
    return this.props.arity === "many"
  },
  getElementType: function() {
    return this.props.elementType
  },
  render: function(){
      const item = this.getItem()
      const key = item.get('key')
      const removeAction = this.props.removeActionFromStagedTrigger
      const updateItemOfStagedTrigger = this.props.updateItemOfStagedTrigger
      const elementType = this.getElementType()

      var deleteButton = ""
      if(this.arityIsMany()){
        deleteButton = <Button bsStyle="danger" onClick={() => removeAction(key)}>Delete</Button>
      }

      var variableForm = ""
      const itemSubTypeId = item.get('itemSubTypeId')
      if(itemSubTypeId){
        const itemSubType = this.itemSubTypeById(itemSubTypeId)
        const variables = _.get(itemSubType, 'variables', [])
        variableForm = variables.map(function(v, index){
            const variableContent = item.getIn(['varAssignments', index])
            return <VariableFormContainer elementType={elementType} index={index} itemKey={key} key={index} currentAssignment={variableContent} variableDetails={v} />
        })
      }

      return <div>
                <FormControl value={itemSubTypeId} componentClass="select" placeholder="select" onChange={(a)=> updateItemOfStagedTrigger(elementType, a.target.value, key)}>
                  <option value="select">select {this.getElementType()}</option>
                  {this.getOptions().map((o) =>
                      <option key={o.title} value={o.id}>{o.title}</option>)}
                </FormControl>
                {deleteButton}
                {variableForm}
             </div>
  }

});

const mapDispatchToProps = (dispatch) => {
    return {
        updateItemOfStagedTrigger: (itemType, itemSubTypeId, key) => {
            var atID = null
            if(itemSubTypeId !== "select"){
                atID = itemSubTypeId
            }
            dispatch(updateItemOfStagedTrigger(itemType, atID, key))
        },
        removeActionFromStagedTrigger: (key) => {
            dispatch(removeActionFromStagedTrigger(key))
        }
    }
}

function mapStateToProps(state) {
  return {};
}

export const TriggerItemContainer = connect(mapStateToProps, mapDispatchToProps)(TriggerItem);