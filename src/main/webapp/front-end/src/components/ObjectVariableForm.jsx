import React from 'react';
import { render } from 'react-dom';
import PureRenderMixin from 'react-addons-pure-render-mixin';
import {connect} from 'react-redux';
import {Modal, Button, FormControl, Panel, Accordion, ButtonGroup, DropdownButton, MenuItem, InputGroup, Form, Col, Row} from 'react-bootstrap';
import {showProps} from '../utilities';
import {Map, List} from 'immutable';
import {updateItemVariableAssignmentInStagedTrigger, removeItemVariableAssignmentInStagedTrigger} from '../action_creators'
import _ from 'lodash';

const ObjectVariableForm = React.createClass({
    mixins: [PureRenderMixin],
    getObjects: function() {
        return this.props.markers || List.of();
    },
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
    render: function() {

        const itemKey = this.getItemKey();

        const updateItemVariableAssignmentInStagedTrigger = this.props.updateItemVariableAssignmentInStagedTrigger
        const removeItemVariableAssignmentInStagedTrigger = this.props.removeItemVariableAssignmentInStagedTrigger

        const selected = this.getSelection() ? this.getSelection().get('objectKey') : "select"
        const arrayIndex = this.getArrayIndex()
        const variableIndex = this.getVariableIndex()

        const elementType = this.getElementType()

        const deleteButton = arrayIndex > 0 ? <Button bsStyle="danger" onClick={() => removeItemVariableAssignmentInStagedTrigger(elementType, itemKey, variableIndex, arrayIndex)}>Delete</Button> : ""

        return <div>
                <Row>
                <Form horizontal>
                  <Col sm={10}>
                  <FormControl value={selected} componentClass="select" placeholder="select" onChange={(a) => updateItemVariableAssignmentInStagedTrigger(elementType, itemKey, variableIndex, arrayIndex, Map({objectKey: a.target.value}))}>
                    <option key={-1} value="select">select object</option>
                    {this.getObjects().map((o) =>
                        <option key={o.get('key')} value={o.get('key')}>{o.get('title')}</option>)}
                  </FormControl>
                  </Col>
                  <Col sm={2}>
                  {deleteButton}
                  </Col>
                </Form>
                </Row>
               </div>
    }
})

const mapDispatchToProps = (dispatch) => {
    return {
            updateItemVariableAssignmentInStagedTrigger: (itemType, itemKey, variableIndex, arrayIndex, assignment) => {
                var newAssignment = null
                if(assignment.get('objectKey') !== "select"){
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
  return {
    markers: state.get('markers')
  };
}

export const ObjectVariableFormContainer = connect(mapStateToProps, mapDispatchToProps)(ObjectVariableForm);