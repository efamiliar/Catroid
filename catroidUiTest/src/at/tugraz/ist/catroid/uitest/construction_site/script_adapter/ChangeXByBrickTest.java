package at.tugraz.ist.catroid.uitest.construction_site.script_adapter;

import java.util.ArrayList;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Smoke;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.constructionSite.content.ProjectManager;
import at.tugraz.ist.catroid.content.brick.Brick;
import at.tugraz.ist.catroid.content.brick.ChangeXByBrick;
import at.tugraz.ist.catroid.content.brick.GoNStepsBackBrick;
import at.tugraz.ist.catroid.content.project.Project;
import at.tugraz.ist.catroid.content.script.Script;
import at.tugraz.ist.catroid.content.sprite.Sprite;
import at.tugraz.ist.catroid.ui.ScriptActivity;

import com.jayway.android.robotium.solo.Solo;

public class ChangeXByBrickTest extends ActivityInstrumentationTestCase2<ScriptActivity>{
	private Solo solo;
	private Project project;
	private ChangeXByBrick changeXByBrick;
	private int xToChange;

	public ChangeXByBrickTest() {
		super("at.tugraz.ist.catroid",ScriptActivity.class);
	}
	
	@Override
    public void setUp() throws Exception {
		createProject();
		solo = new Solo(getInstrumentation(), getActivity());
	}
	
	@Override
    public void tearDown() throws Exception {	
		try {	
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		getActivity().finish();
		super.tearDown();
	}
	
	@Smoke
	public void testChangeXByBrick() throws Throwable {
		int childrenCount = getActivity().getAdapter().getChildCountFromLastGroup();
		int groupCount = getActivity().getAdapter().getGroupCount();

		assertEquals("Incorrect number of bricks.", 2, solo.getCurrentListViews().get(0).getChildCount());
		assertEquals("Incorrect number of bricks.", 1, childrenCount);
		
		ArrayList<Brick> projectBrickList = project.getSpriteList().get(0).getScriptList().get(0).getBrickList();
		assertEquals("Incorrect number of bricks.", 1, projectBrickList.size());
		
		assertEquals("Wrong Brick instance.", projectBrickList.get(0), getActivity().getAdapter().getChild(groupCount-1,
				      0));
		assertNotNull("TextView does not exist.", solo.getText(getActivity().getString(R.string.change_x_main_adapter)));
		
		solo.clickOnEditText(0);
		solo.clearEditText(0);
		solo.enterText(0, xToChange + "");
		solo.clickOnButton(0);
		
		Thread.sleep(300);
		assertEquals("Wrong text in field.", xToChange, changeXByBrick.getXMovement());
		assertEquals("Value in Brick is not updated.", xToChange+"", solo.getEditText(0).getText().toString());
	}
	
	private void createProject() {
		xToChange = 17;
		project = new Project(null, "testProject");
        Sprite sprite = new Sprite("cat");
        Script script = new Script("script", sprite);
        changeXByBrick = new ChangeXByBrick(sprite, 0);
        script.addBrick(changeXByBrick);

        sprite.getScriptList().add(script);
        project.addSprite(sprite);
        
        ProjectManager.getInstance().setProject(project);
        ProjectManager.getInstance().setCurrentSprite(sprite);
        ProjectManager.getInstance().setCurrentScript(script);
	}
}