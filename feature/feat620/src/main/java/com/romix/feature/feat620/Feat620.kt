package com.romix.feature.feat620

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat620Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat620UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat620FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat620UserSummary
)

data class Feat620UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat620NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat620Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat620Config = Feat620Config()
) {

    fun loadSnapshot(userId: Long): Feat620NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat620NetworkSnapshot(
            users = listOf(user),
            posts = posts,
            rawHash = hash
        )
    }

    private fun snapshotChecksum(user: ApiUserDto, posts: List<ApiPostDto>): Int {
        var result = 1
        result = 31 * result + user.id.hashCode()
        result = 31 * result + user.name.hashCode()
        for (post in posts) {
            result = 31 * result + post.id.hashCode()
            result = 31 * result + post.title.hashCode()
        }
        return result
    }

    fun toUserSummary(coreUser: CoreUser): Feat620UserSummary {
        return Feat620UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat620FeedItem> {
        val result = java.util.ArrayList<Feat620FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat620FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat620UiMapper {

    fun mapToUi(model: List<Feat620FeedItem>): Feat620UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat620UiModel(
            header = UiText("Feat620 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat620UiModel =
        Feat620UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat620UiModel =
        Feat620UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat620UiModel =
        Feat620UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat620Service(
    private val repository: Feat620Repository,
    private val uiMapper: Feat620UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat620UiModel {
        val snapshot = repository.loadSnapshot(userId)
        if (snapshot.users.isEmpty()) {
            return uiMapper.emptyState()
        }
        val coreUser = CoreUser(
            id = snapshot.users[0].id,
            name = snapshot.users[0].name,
            email = null,
            isActive = true
        )
        val items = repository.toFeedItems(listOf(coreUser))
        return uiMapper.mapToUi(items)
    }

    fun ping(path: String): Int {
        val request = NetworkRequest(
            path = path,
            method = "GET",
            body = null
        )
        val response = networkClient.execute(request)
        return response.code
    }

    fun demoComplexFlow(usersCount: Int): Feat620UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat620UserItem1(val user: CoreUser, val label: String)
data class Feat620UserItem2(val user: CoreUser, val label: String)
data class Feat620UserItem3(val user: CoreUser, val label: String)
data class Feat620UserItem4(val user: CoreUser, val label: String)
data class Feat620UserItem5(val user: CoreUser, val label: String)
data class Feat620UserItem6(val user: CoreUser, val label: String)
data class Feat620UserItem7(val user: CoreUser, val label: String)
data class Feat620UserItem8(val user: CoreUser, val label: String)
data class Feat620UserItem9(val user: CoreUser, val label: String)
data class Feat620UserItem10(val user: CoreUser, val label: String)

data class Feat620StateBlock1(val state: Feat620UiModel, val checksum: Int)
data class Feat620StateBlock2(val state: Feat620UiModel, val checksum: Int)
data class Feat620StateBlock3(val state: Feat620UiModel, val checksum: Int)
data class Feat620StateBlock4(val state: Feat620UiModel, val checksum: Int)
data class Feat620StateBlock5(val state: Feat620UiModel, val checksum: Int)
data class Feat620StateBlock6(val state: Feat620UiModel, val checksum: Int)
data class Feat620StateBlock7(val state: Feat620UiModel, val checksum: Int)
data class Feat620StateBlock8(val state: Feat620UiModel, val checksum: Int)
data class Feat620StateBlock9(val state: Feat620UiModel, val checksum: Int)
data class Feat620StateBlock10(val state: Feat620UiModel, val checksum: Int)

fun buildFeat620UserItem(user: CoreUser, index: Int): Feat620UserItem1 {
    return Feat620UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat620StateBlock(model: Feat620UiModel): Feat620StateBlock1 {
    return Feat620StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat620UserSummary> {
    val list = java.util.ArrayList<Feat620UserSummary>(users.size)
    for (user in users) {
        list += Feat620UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat620UserSummary>): List<UiListItem> {
    val items = java.util.ArrayList<UiListItem>(summaries.size)
    for ((index, summary) in summaries.withIndex()) {
        items += UiListItem(
            id = index.toLong(),
            title = summary.name,
            subtitle = if (summary.isActive) "Active" else "Inactive",
            selected = summary.isActive
        )
    }
    return items
}

fun createLargeUiModel(count: Int): Feat620UiModel {
    val summaries = (0 until count).map {
        Feat620UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat620UiModel(
        header = UiText("Large model $count"),
        items = items,
        loading = false,
        error = null
    )
}

fun buildSequentialUsers(count: Int): List<CoreUser> {
    val list = java.util.ArrayList<CoreUser>(count)
    for (i in 0 until count) {
        list += CoreUser(
            id = i.toLong(),
            name = "User-$i",
            email = null,
            isActive = i % 3 != 0
        )
    }
    return list
}

fun mapToUiTextList(users: List<CoreUser>): List<UiText> {
    val list = java.util.ArrayList<UiText>(users.size)
    for (user in users) {
        list += UiText("User: ${user.name}")
    }
    return list
}

fun buildManyUiModels(repeat: Int): List<Feat620UiModel> {
    val models = java.util.ArrayList<Feat620UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat620AnalyticsEvent1(val name: String, val value: String)
data class Feat620AnalyticsEvent2(val name: String, val value: String)
data class Feat620AnalyticsEvent3(val name: String, val value: String)
data class Feat620AnalyticsEvent4(val name: String, val value: String)
data class Feat620AnalyticsEvent5(val name: String, val value: String)
data class Feat620AnalyticsEvent6(val name: String, val value: String)
data class Feat620AnalyticsEvent7(val name: String, val value: String)
data class Feat620AnalyticsEvent8(val name: String, val value: String)
data class Feat620AnalyticsEvent9(val name: String, val value: String)
data class Feat620AnalyticsEvent10(val name: String, val value: String)

fun logFeat620Event1(event: Feat620AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat620Event2(event: Feat620AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat620Event3(event: Feat620AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat620Event4(event: Feat620AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat620Event5(event: Feat620AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat620Event6(event: Feat620AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat620Event7(event: Feat620AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat620Event8(event: Feat620AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat620Event9(event: Feat620AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat620Event10(event: Feat620AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat620Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat620Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat620Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat620Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat620Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat620Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat620Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat620Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat620Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat620Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat620(u: CoreUser): Feat620Projection1 =
    Feat620Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat620Projection1> {
    val list = java.util.ArrayList<Feat620Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat620(u)
    }
    return list
}
