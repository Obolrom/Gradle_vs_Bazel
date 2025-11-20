package com.romix.feature.feat279

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat279Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat279UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat279FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat279UserSummary
)

data class Feat279UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat279NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat279Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat279Config = Feat279Config()
) {

    fun loadSnapshot(userId: Long): Feat279NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat279NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat279UserSummary {
        return Feat279UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat279FeedItem> {
        val result = java.util.ArrayList<Feat279FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat279FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat279UiMapper {

    fun mapToUi(model: List<Feat279FeedItem>): Feat279UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat279UiModel(
            header = UiText("Feat279 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat279UiModel =
        Feat279UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat279UiModel =
        Feat279UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat279UiModel =
        Feat279UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat279Service(
    private val repository: Feat279Repository,
    private val uiMapper: Feat279UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat279UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat279UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat279UserItem1(val user: CoreUser, val label: String)
data class Feat279UserItem2(val user: CoreUser, val label: String)
data class Feat279UserItem3(val user: CoreUser, val label: String)
data class Feat279UserItem4(val user: CoreUser, val label: String)
data class Feat279UserItem5(val user: CoreUser, val label: String)
data class Feat279UserItem6(val user: CoreUser, val label: String)
data class Feat279UserItem7(val user: CoreUser, val label: String)
data class Feat279UserItem8(val user: CoreUser, val label: String)
data class Feat279UserItem9(val user: CoreUser, val label: String)
data class Feat279UserItem10(val user: CoreUser, val label: String)

data class Feat279StateBlock1(val state: Feat279UiModel, val checksum: Int)
data class Feat279StateBlock2(val state: Feat279UiModel, val checksum: Int)
data class Feat279StateBlock3(val state: Feat279UiModel, val checksum: Int)
data class Feat279StateBlock4(val state: Feat279UiModel, val checksum: Int)
data class Feat279StateBlock5(val state: Feat279UiModel, val checksum: Int)
data class Feat279StateBlock6(val state: Feat279UiModel, val checksum: Int)
data class Feat279StateBlock7(val state: Feat279UiModel, val checksum: Int)
data class Feat279StateBlock8(val state: Feat279UiModel, val checksum: Int)
data class Feat279StateBlock9(val state: Feat279UiModel, val checksum: Int)
data class Feat279StateBlock10(val state: Feat279UiModel, val checksum: Int)

fun buildFeat279UserItem(user: CoreUser, index: Int): Feat279UserItem1 {
    return Feat279UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat279StateBlock(model: Feat279UiModel): Feat279StateBlock1 {
    return Feat279StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat279UserSummary> {
    val list = java.util.ArrayList<Feat279UserSummary>(users.size)
    for (user in users) {
        list += Feat279UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat279UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat279UiModel {
    val summaries = (0 until count).map {
        Feat279UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat279UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat279UiModel> {
    val models = java.util.ArrayList<Feat279UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat279AnalyticsEvent1(val name: String, val value: String)
data class Feat279AnalyticsEvent2(val name: String, val value: String)
data class Feat279AnalyticsEvent3(val name: String, val value: String)
data class Feat279AnalyticsEvent4(val name: String, val value: String)
data class Feat279AnalyticsEvent5(val name: String, val value: String)
data class Feat279AnalyticsEvent6(val name: String, val value: String)
data class Feat279AnalyticsEvent7(val name: String, val value: String)
data class Feat279AnalyticsEvent8(val name: String, val value: String)
data class Feat279AnalyticsEvent9(val name: String, val value: String)
data class Feat279AnalyticsEvent10(val name: String, val value: String)

fun logFeat279Event1(event: Feat279AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat279Event2(event: Feat279AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat279Event3(event: Feat279AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat279Event4(event: Feat279AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat279Event5(event: Feat279AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat279Event6(event: Feat279AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat279Event7(event: Feat279AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat279Event8(event: Feat279AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat279Event9(event: Feat279AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat279Event10(event: Feat279AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat279Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat279Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat279Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat279Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat279Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat279Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat279Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat279Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat279Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat279Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat279(u: CoreUser): Feat279Projection1 =
    Feat279Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat279Projection1> {
    val list = java.util.ArrayList<Feat279Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat279(u)
    }
    return list
}
