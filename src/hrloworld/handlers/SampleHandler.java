package hrloworld.handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.osate.aadl2.AadlInteger;
import org.osate.aadl2.BusImplementation;
import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.Element;
import org.osate.aadl2.IntegerLiteral;
import org.osate.aadl2.Property;
import org.osate.aadl2.PropertyAssociation;
import org.osate.aadl2.Subcomponent;
import org.osate.aadl2.VirtualBusImplementation;
import org.osate.xtext.aadl2.properties.util.EMFIndexRetrieval;
import org.osate.xtext.aadl2.properties.util.PropertyUtils;

public class SampleHandler extends ModifyingAadlHandler {
	@Override
	protected IStatus runJob(Element sel, IProgressMonitor monitor) {
		for (ComponentImplementation ci : getAllComponentImplementations(sel)) {
			if (!(ci instanceof BusImplementation) && !(ci instanceof VirtualBusImplementation)) {
				addRandomDataSize(ci);
			}
		}
		return Status.OK_STATUS;
	}

	private List<ComponentImplementation> getAllComponentImplementations(Element sel) {
		if (sel instanceof ComponentImplementation) {
			ComponentImplementation ci = (ComponentImplementation) sel;
			List<ComponentImplementation> result = new ArrayList<>();
			result.add(ci);
			for (Subcomponent sub : ci.getOwnedSubcomponents()) {
				result.addAll(getAllComponentImplementations(sub.getAllClassifier()));
			}
			return result;
		} else {
			return Collections.emptyList();
		}
	}

	private void addRandomDataSize(ComponentImplementation ci) {
		PropertyAssociation pa = ci.createOwnedPropertyAssociation();

		Property prop = EMFIndexRetrieval.getPropertyDefinitionInWorkspace(ci, "Data_Size");
		pa.setProperty(prop);

		IntegerLiteral value = PropertyUtils.createIntegerValue(new Random().nextInt());
		AadlInteger sizeTy = (AadlInteger) EMFIndexRetrieval.getPropertyTypeInWorkspace("Size");
		value.setUnit(sizeTy.getUnitsType().findLiteral("KByte"));

		pa.createOwnedValue().setOwnedValue(value);
	}

	@Override
	protected String getJobName() {
		return "HRLoWorld Sample Job";
	}
}
