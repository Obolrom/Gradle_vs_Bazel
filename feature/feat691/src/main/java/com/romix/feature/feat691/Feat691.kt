package com.romix.feature.feat691

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat691Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat691UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat691FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat691UserSummary
)

data class Feat691UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat691NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat691Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat691Config = Feat691Config()
) {

    fun loadSnapshot(userId: Long): Feat691NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat691NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat691UserSummary {
        return Feat691UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat691FeedItem> {
        val result = java.util.ArrayList<Feat691FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat691FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat691UiMapper {

    fun mapToUi(model: List<Feat691FeedItem>): Feat691UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat691UiModel(
            header = UiText("Feat691 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat691UiModel =
        Feat691UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat691UiModel =
        Feat691UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat691UiModel =
        Feat691UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat691Service(
    private val repository: Feat691Repository,
    private val uiMapper: Feat691UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat691UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat691UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat691UserItem1(val user: CoreUser, val label: String)
data class Feat691UserItem2(val user: CoreUser, val label: String)
data class Feat691UserItem3(val user: CoreUser, val label: String)
data class Feat691UserItem4(val user: CoreUser, val label: String)
data class Feat691UserItem5(val user: CoreUser, val label: String)
data class Feat691UserItem6(val user: CoreUser, val label: String)
data class Feat691UserItem7(val user: CoreUser, val label: String)
data class Feat691UserItem8(val user: CoreUser, val label: String)
data class Feat691UserItem9(val user: CoreUser, val label: String)
data class Feat691UserItem10(val user: CoreUser, val label: String)

data class Feat691StateBlock1(val state: Feat691UiModel, val checksum: Int)
data class Feat691StateBlock2(val state: Feat691UiModel, val checksum: Int)
data class Feat691StateBlock3(val state: Feat691UiModel, val checksum: Int)
data class Feat691StateBlock4(val state: Feat691UiModel, val checksum: Int)
data class Feat691StateBlock5(val state: Feat691UiModel, val checksum: Int)
data class Feat691StateBlock6(val state: Feat691UiModel, val checksum: Int)
data class Feat691StateBlock7(val state: Feat691UiModel, val checksum: Int)
data class Feat691StateBlock8(val state: Feat691UiModel, val checksum: Int)
data class Feat691StateBlock9(val state: Feat691UiModel, val checksum: Int)
data class Feat691StateBlock10(val state: Feat691UiModel, val checksum: Int)

fun buildFeat691UserItem(user: CoreUser, index: Int): Feat691UserItem1 {
    return Feat691UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat691StateBlock(model: Feat691UiModel): Feat691StateBlock1 {
    return Feat691StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat691UserSummary> {
    val list = java.util.ArrayList<Feat691UserSummary>(users.size)
    for (user in users) {
        list += Feat691UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat691UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat691UiModel {
    val summaries = (0 until count).map {
        Feat691UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat691UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat691UiModel> {
    val models = java.util.ArrayList<Feat691UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat691AnalyticsEvent1(val name: String, val value: String)
data class Feat691AnalyticsEvent2(val name: String, val value: String)
data class Feat691AnalyticsEvent3(val name: String, val value: String)
data class Feat691AnalyticsEvent4(val name: String, val value: String)
data class Feat691AnalyticsEvent5(val name: String, val value: String)
data class Feat691AnalyticsEvent6(val name: String, val value: String)
data class Feat691AnalyticsEvent7(val name: String, val value: String)
data class Feat691AnalyticsEvent8(val name: String, val value: String)
data class Feat691AnalyticsEvent9(val name: String, val value: String)
data class Feat691AnalyticsEvent10(val name: String, val value: String)

fun logFeat691Event1(event: Feat691AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat691Event2(event: Feat691AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat691Event3(event: Feat691AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat691Event4(event: Feat691AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat691Event5(event: Feat691AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat691Event6(event: Feat691AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat691Event7(event: Feat691AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat691Event8(event: Feat691AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat691Event9(event: Feat691AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat691Event10(event: Feat691AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat691Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat691Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat691Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat691Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat691Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat691Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat691Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat691Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat691Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat691Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat691(u: CoreUser): Feat691Projection1 =
    Feat691Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat691Projection1> {
    val list = java.util.ArrayList<Feat691Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat691(u)
    }
    return list
}
