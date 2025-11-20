package com.romix.feature.feat174

import com.romix.core.model.*
import com.romix.core.ui.*
import com.romix.core.network.*
import java.util.ArrayList
import java.util.Collections.emptyList

class Feat174Config(
    val pageSize: Int = 20,
    val enableLogging: Boolean = true
)

data class Feat174UserSummary(
    val id: Long,
    val name: String,
    val checksum: Int,
    val isActive: Boolean
)

data class Feat174FeedItem(
    val id: Long,
    val title: String,
    val subtitle: String?,
    val userSummary: Feat174UserSummary
)

data class Feat174UiModel(
    val header: UiText,
    val items: List<UiListItem>,
    val loading: Boolean,
    val error: String?
)

data class Feat174NetworkSnapshot(
    val users: List<ApiUserDto>,
    val posts: List<ApiPostDto>,
    val rawHash: Int
)

class Feat174Repository(
    private val api: FakeApiService = FakeApiService(
        FakeNetworkClient()
    ),
    private val config: Feat174Config = Feat174Config()
) {

    fun loadSnapshot(userId: Long): Feat174NetworkSnapshot {
        val user = api.getUser(userId)
        val posts = api.getPosts(userId, config.pageSize)
        val hash = snapshotChecksum(user, posts)
        return Feat174NetworkSnapshot(
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

    fun toUserSummary(coreUser: CoreUser): Feat174UserSummary {
        return Feat174UserSummary(
            id = coreUser.id,
            name = coreUser.name,
            checksum = computeChecksum(coreUser.name),
            isActive = coreUser.isActive
        )
    }

    fun toFeedItems(users: List<CoreUser>): List<Feat174FeedItem> {
        val result = java.util.ArrayList<Feat174FeedItem>()
        var idCounter = 0L
        for (user in users) {
            val summary = toUserSummary(user)
            val title = "User ${summary.name}"
            val subtitle = if (summary.isActive) "Active" else "Inactive"
            result += Feat174FeedItem(
                id = idCounter++,
                title = title,
                subtitle = subtitle,
                userSummary = summary
            )
        }
        return result
    }
}

class Feat174UiMapper {

    fun mapToUi(model: List<Feat174FeedItem>): Feat174UiModel {
        val items = model.mapIndexed { index, item ->
            UiListItem(
                id = item.id,
                title = "${index + 1}. ${item.title}",
                subtitle = item.subtitle,
                selected = item.userSummary.isActive
            )
        }
        return Feat174UiModel(
            header = UiText("Feat174 Feed (${model.size})"),
            items = items,
            loading = false,
            error = null
        )
    }

    fun emptyState(): Feat174UiModel =
        Feat174UiModel(
            header = UiText("No data"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = null
        )

    fun loadingState(): Feat174UiModel =
        Feat174UiModel(
            header = UiText("Loading..."),
            items = java.util.Collections.emptyList(),
            loading = true,
            error = null
        )

    fun errorState(message: String): Feat174UiModel =
        Feat174UiModel(
            header = UiText("Error"),
            items = java.util.Collections.emptyList(),
            loading = false,
            error = message
        )
}

class Feat174Service(
    private val repository: Feat174Repository,
    private val uiMapper: Feat174UiMapper,
    private val networkClient: FakeNetworkClient,
    private val apiService: FakeApiService
) {

    fun buildUiForUser(userId: Long): Feat174UiModel {
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

    fun demoComplexFlow(usersCount: Int): Feat174UiModel {
        val users = (0 until usersCount).map {
            CoreModelFactory.createUser(it)
        }
        val items = repository.toFeedItems(users)
        return uiMapper.mapToUi(items)
    }
}

data class Feat174UserItem1(val user: CoreUser, val label: String)
data class Feat174UserItem2(val user: CoreUser, val label: String)
data class Feat174UserItem3(val user: CoreUser, val label: String)
data class Feat174UserItem4(val user: CoreUser, val label: String)
data class Feat174UserItem5(val user: CoreUser, val label: String)
data class Feat174UserItem6(val user: CoreUser, val label: String)
data class Feat174UserItem7(val user: CoreUser, val label: String)
data class Feat174UserItem8(val user: CoreUser, val label: String)
data class Feat174UserItem9(val user: CoreUser, val label: String)
data class Feat174UserItem10(val user: CoreUser, val label: String)

data class Feat174StateBlock1(val state: Feat174UiModel, val checksum: Int)
data class Feat174StateBlock2(val state: Feat174UiModel, val checksum: Int)
data class Feat174StateBlock3(val state: Feat174UiModel, val checksum: Int)
data class Feat174StateBlock4(val state: Feat174UiModel, val checksum: Int)
data class Feat174StateBlock5(val state: Feat174UiModel, val checksum: Int)
data class Feat174StateBlock6(val state: Feat174UiModel, val checksum: Int)
data class Feat174StateBlock7(val state: Feat174UiModel, val checksum: Int)
data class Feat174StateBlock8(val state: Feat174UiModel, val checksum: Int)
data class Feat174StateBlock9(val state: Feat174UiModel, val checksum: Int)
data class Feat174StateBlock10(val state: Feat174UiModel, val checksum: Int)

fun buildFeat174UserItem(user: CoreUser, index: Int): Feat174UserItem1 {
    return Feat174UserItem1(
        user = user,
        label = "User(${user.id}) idx=$index"
    )
}

fun buildFeat174StateBlock(model: Feat174UiModel): Feat174StateBlock1 {
    return Feat174StateBlock1(
        state = model,
        checksum = computeChecksum(model.header.value + (model.error ?: ""))
    )
}

fun transformUsersToSummaries(users: List<CoreUser>): List<Feat174UserSummary> {
    val list = java.util.ArrayList<Feat174UserSummary>(users.size)
    for (user in users) {
        list += Feat174UserSummary(
            id = user.id,
            name = user.name,
            checksum = computeChecksum(user.name),
            isActive = user.isActive
        )
    }
    return list
}

fun mapSummariesToUiItems(summaries: List<Feat174UserSummary>): List<UiListItem> {
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

fun createLargeUiModel(count: Int): Feat174UiModel {
    val summaries = (0 until count).map {
        Feat174UserSummary(
            id = it.toLong(),
            name = "User-$it",
            checksum = it * 17,
            isActive = it % 2 == 0
        )
    }
    val items = mapSummariesToUiItems(summaries)
    return Feat174UiModel(
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

fun buildManyUiModels(repeat: Int): List<Feat174UiModel> {
    val models = java.util.ArrayList<Feat174UiModel>(repeat)
    for (i in 0 until repeat) {
        val count = (i % 20) + 1
        models += createLargeUiModel(count)
    }
    return models
}

data class Feat174AnalyticsEvent1(val name: String, val value: String)
data class Feat174AnalyticsEvent2(val name: String, val value: String)
data class Feat174AnalyticsEvent3(val name: String, val value: String)
data class Feat174AnalyticsEvent4(val name: String, val value: String)
data class Feat174AnalyticsEvent5(val name: String, val value: String)
data class Feat174AnalyticsEvent6(val name: String, val value: String)
data class Feat174AnalyticsEvent7(val name: String, val value: String)
data class Feat174AnalyticsEvent8(val name: String, val value: String)
data class Feat174AnalyticsEvent9(val name: String, val value: String)
data class Feat174AnalyticsEvent10(val name: String, val value: String)

fun logFeat174Event1(event: Feat174AnalyticsEvent1): String = "${event.name}:${event.value}"
fun logFeat174Event2(event: Feat174AnalyticsEvent2): String = "${event.name}:${event.value}"
fun logFeat174Event3(event: Feat174AnalyticsEvent3): String = "${event.name}:${event.value}"
fun logFeat174Event4(event: Feat174AnalyticsEvent4): String = "${event.name}:${event.value}"
fun logFeat174Event5(event: Feat174AnalyticsEvent5): String = "${event.name}:${event.value}"
fun logFeat174Event6(event: Feat174AnalyticsEvent6): String = "${event.name}:${event.value}"
fun logFeat174Event7(event: Feat174AnalyticsEvent7): String = "${event.name}:${event.value}"
fun logFeat174Event8(event: Feat174AnalyticsEvent8): String = "${event.name}:${event.value}"
fun logFeat174Event9(event: Feat174AnalyticsEvent9): String = "${event.name}:${event.value}"
fun logFeat174Event10(event: Feat174AnalyticsEvent10): String = "${event.name}:${event.value}"

data class Feat174Projection1(val id: Long, val label: String, val active: Boolean)
data class Feat174Projection2(val id: Long, val label: String, val active: Boolean)
data class Feat174Projection3(val id: Long, val label: String, val active: Boolean)
data class Feat174Projection4(val id: Long, val label: String, val active: Boolean)
data class Feat174Projection5(val id: Long, val label: String, val active: Boolean)
data class Feat174Projection6(val id: Long, val label: String, val active: Boolean)
data class Feat174Projection7(val id: Long, val label: String, val active: Boolean)
data class Feat174Projection8(val id: Long, val label: String, val active: Boolean)
data class Feat174Projection9(val id: Long, val label: String, val active: Boolean)
data class Feat174Projection10(val id: Long, val label: String, val active: Boolean)

fun projectUserToFeat174(u: CoreUser): Feat174Projection1 =
    Feat174Projection1(
        id = u.id,
        label = u.name,
        active = u.isActive
    )

fun bulkProjectUsers(users: List<CoreUser>): List<Feat174Projection1> {
    val list = java.util.ArrayList<Feat174Projection1>(users.size)
    for (u in users) {
        list += projectUserToFeat174(u)
    }
    return list
}
