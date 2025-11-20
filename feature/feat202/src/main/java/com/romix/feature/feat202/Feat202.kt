package com.romix.feature.feat202

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat202Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat202UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat202FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat202UserSummary
)

data class Feat202UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat202NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat202Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat202Config = Feat202Config()
) {

    fun loadSnapshot(userId: Long): Feat202NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat202NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat202UserSummary {
        return Feat202UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat202FeedItem> {
        val result = java.util.ArrayList<Feat202FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat202FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat202UiMapper {

    fun mapToUi(model: List<Feat202FeedItem>): Feat202UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat202UiModel(
            header = UiText("Feat202 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat202UiModel =
        Feat202UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat202UiModel =
        Feat202UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat202UiModel =
        Feat202UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat202Service(
    private val repository: Feat202Repository,
    private val uiMapper: Feat202UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat202UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat202UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat202UserItem1(val user: CoreUser, val label: String)
data class Feat202UserItem2(val user: CoreUser, val label: String)
data class Feat202UserItem3(val user: CoreUser, val label: String)
data class Feat202UserItem4(val user: CoreUser, val label: String)
data class Feat202UserItem5(val user: CoreUser, val label: String)
data class Feat202UserItem6(val user: CoreUser, val label: String)
data class Feat202UserItem7(val user: CoreUser, val label: String)
data class Feat202UserItem8(val user: CoreUser, val label: String)
data class Feat202UserItem9(val user: CoreUser, val label: String)
data class Feat202UserItem10(val user: CoreUser, val label: String)

data class Feat202StateBlock1(val state: Feat202UiModel, val checksum: Int)
data class Feat202StateBlock2(val state: Feat202UiModel, val checksum: Int)
data class Feat202StateBlock3(val state: Feat202UiModel, val checksum: Int)
data class Feat202StateBlock4(val state: Feat202UiModel, val checksum: Int)
data class Feat202StateBlock5(val state: Feat202UiModel, val checksum: Int)
data class Feat202StateBlock6(val state: Feat202UiModel, val checksum: Int)
data class Feat202StateBlock7(val state: Feat202UiModel, val checksum: Int)
data class Feat202StateBlock8(val state: Feat202UiModel, val checksum: Int)
data class Feat202StateBlock9(val state: Feat202UiModel, val checksum: Int)
data class Feat202StateBlock10(val state: Feat202UiModel, val checksum: Int)

fun buildFeat202UserItem(user: CoreUser, index: Int): Feat202UserItem1 {
    return Feat202UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat202StateBlock(model: Feat202UiModel): Feat202StateBlock1 {
    return Feat202StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat202UserSummary> {
    val list = java.util.ArrayList<Feat202UserSummary>(users.size)
    for (user in users) {
        list += Feat202UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat202UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat202UiModel {
    val summaries = (0 until count).map {
        Feat202UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat202UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat202UiModel> {
    val models = java.util.ArrayList<Feat202UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat202AnalyticsEvent1(val name: String, val value: String)
data class Feat202AnalyticsEvent2(val name: String, val value: String)
data class Feat202AnalyticsEvent3(val name: String, val value: String)
data class Feat202AnalyticsEvent4(val name: String, val value: String)
data class Feat202AnalyticsEvent5(val name: String, val value: String)
data class Feat202AnalyticsEvent6(val name: String, val value: String)
data class Feat202AnalyticsEvent7(val name: String, val value: String)
data class Feat202AnalyticsEvent8(val name: String, val value: String)
data class Feat202AnalyticsEvent9(val name: String, val value: String)
data class Feat202AnalyticsEvent10(val name: String, val value: String)

fun logFeat202Event1(event: Feat202AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat202Event2(event: Feat202AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat202Event3(event: Feat202AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat202Event4(event: Feat202AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat202Event5(event: Feat202AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat202Event6(event: Feat202AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat202Event7(event: Feat202AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat202Event8(event: Feat202AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat202Event9(event: Feat202AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat202Event10(event: Feat202AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat202Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat202Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat202Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat202Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat202Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat202Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat202Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat202Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat202Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat202Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat202(u: CoreUser): Feat202Projection1 =
    Feat202Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat202Projection1> {
    val list = java.util.ArrayList<Feat202Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat202(u)
    }
    return list
}
