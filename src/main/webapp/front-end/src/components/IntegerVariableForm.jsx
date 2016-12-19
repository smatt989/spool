import React from 'react';
import { render } from 'react-dom';
import PureRenderMixin from 'react-addons-pure-render-mixin';
import {connect} from 'react-redux';
import {FormControl, FormGroup, Col, Row} from 'react-bootstrap';
import {showProps} from '../utilities';
import {Map, List} from 'immutable';
import {updateItemVariableAssignmentInStagedTrigger, removeItemVariableAssignmentInStagedTrigger} from '../action_creators'
import _ from 'lodash';

const IntegerVariableForm = React.createClass({
    mixins: [PureRenderMixin],
    getSelection: function() {
        return this.props.selection || null;
    },
    getItemKey: function() {
        return this.props.itemKey || null;
    },
    getArrayIndex: function() {
        return this.props.arrayIndex || 0;
    },
    getVariableIndex: function(){
        return this.props.variableIndex || 0;
    },
    getElementType: function() {
        return this.props.elementType || null
    },
    getSelectionValue: function(){
       return this.getSelection() ? this.getSelection().get('integer') : ""
    },
    getValidationState: function() {
          const integer = this.getSelectionValue();
          if (Number.isInteger(integer)) return 'success';
          else return 'error';
    },
    render: function() {

        const itemKey = this.getItemKey();

        const updateItemVariableAssignmentInStagedTrigger = this.props.updateItemVariableAssignmentInStagedTrigger
        const removeItemVariableAssignmentInStagedTrigger = this.props.removeItemVariableAssignmentInStagedTrigger

        const selected = this.getSelectionValue()
        const arrayIndex = this.getArrayIndex()
        const variableIndex = this.getVariableIndex()

        const elementType = this.getElementType()

        const deleteButton = arrayIndex > 0 ? <Button bsStyle="danger" onClick={() => removeItemVariableAssignmentInStagedTrigger(elementType, itemKey, variableIndex, arrayIndex)}>Delete</Button> : ""

        const onChangeFunction = function(a){
            updateItemVariableAssignmentInStagedTrigger(elementType, itemKey, variableIndex, arrayIndex, Map({integer: parseInt(a.target.value)}))
        }

        return <div>
                  <Row>
                  <Col sm={10}>
                        <FormGroup controlId="integerForm" validationState={this.getValidationState()}>
                          <FormControl type="text" value={selected} placeholder="Enter integer" onChange={onChangeFunction} />
                          <FormControl.Feedback />
                        </FormGroup>
                  </Col>
                  <Col sm={2}>
                  {deleteButton}
                  </Col>
                  </Row>
               </div>
    }
})

const mapDispatchToProps = (dispatch) => {
    return {
            updateItemVariableAssignmentInStagedTrigger: (itemType, itemKey, variableIndex, arrayIndex, assignment) => {
                var newAssignment = null
                if(Number.isInteger(assignment.get('integer'))){
                    newAssignment = assignment
                }
                dispatch(updateItemVariableAssignmentInStagedTrigger(itemType, itemKey, variableIndex, arrayIndex, newAssignment))
        },
            removeItemVariableAssignmentInStagedTrigger: (itemType, itemKey, variableIndex, arrayIndex) => {
                dispatch(removeItemVariableAssignmentInStagedTrigger(itemType, itemKey, variableIndex, arrayIndex))
        }
    }
}

function mapStateToProps(state) {
  return {};
}

export const IntegerVariableFormContainer = connect(mapStateToProps, mapDispatchToProps)(IntegerVariableForm);